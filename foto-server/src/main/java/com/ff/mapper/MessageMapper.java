package com.ff.mapper;

import com.ff.dto.ContactUserVO;
import com.ff.entity.Message;
import com.ff.entity.User;
import org.apache.ibatis.annotations.*;


import java.util.List;

@Mapper
public interface MessageMapper {
    //根据用户id查询用户信息
    @Select("select * from user where id = #{userId}")
    User selectUserById(Long userId);

    //根据用户id和工作id查询私信信息,通过时间排序，时间最早的在最上面
    @Select("select * from chat_message where (from_user_id = #{myId} and to_user_id = #{userId}) or (from_user_id = #{userId} and to_user_id = #{myId}) order by create_time asc")
    List<Message> getMessageList(Long myId, Long userId);


    //插入私信信息
    @Insert("insert into chat_message (from_user_id, to_user_id, content) values (#{fromUserId}, #{toUserId}, #{content})")
    void insert(Message message);

    //查询所有联系过我的用户
    @Select("SELECT\n" +
            "    u.id,\n" +
            "    u.username,\n" +
            "    u.avatar_url,\n" +
            "    IFNULL((\n" +
            "        SELECT COUNT(*)\n" +
            "        FROM chat_message m2\n" +
            "        WHERE m2.to_user_id = #{myId}\n" +
            "          AND m2.from_user_id = u.id\n" +
            "          AND m2.is_read = 0\n" +
            "    ), 0) AS unreadCount\n" +
            "FROM user u\n" +
            "WHERE u.id IN (\n" +
            "    SELECT DISTINCT user_id FROM (\n" +
            "        SELECT to_user_id AS user_id FROM chat_message WHERE from_user_id = #{myId}\n" +
            "        UNION\n" +
            "        SELECT from_user_id AS user_id FROM chat_message WHERE to_user_id = #{myId}\n" +
            "    ) AS temp\n" +
            ")")
    List<ContactUserVO> getAllContact(Long myId);

    //标记未读消息为已读
    @Update("update chat_message set is_read = 1 where to_user_id = #{myId} and from_user_id = #{userId}")
    void markAllRead(Long myId, Long userId);

    //关注用户，使他成为我的好友
    @Insert("insert into friend (user_id, friend_id) values (#{myId}, #{userId})")
    void follow(Long myId, Long userId);

    //取消关注
    @Delete("delete from friend where user_id = #{myId} and friend_id = #{userId}")
    void unfollow(Long myId, Long userId);

    //获取关注的用户列表
    @Select("""
        SELECT u.id, u.username, u.avatar_url
        FROM friend f
        JOIN user u ON f.friend_id = u.id
        WHERE f.user_id = #{myId}
""")
    List<User> atUser(Long myId);

    //增加他的粉丝数目
    @Update("update user set fans_count = fans_count + 1 where id = #{userId}")
    void addFan(Long userId);

    //减少他的粉丝数目
    @Update("update user set fans_count = fans_count - 1 where id = #{userId}")
    void subFan(Long userId);
}
