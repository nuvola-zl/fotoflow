create table if not exists fotoflow.barrage
(
    id          bigint auto_increment comment '主键'
    primary key,
    video_id    bigint                                not null comment '视频ID（对应你的视频表id）',
    user_id     bigint                                not null comment '发送用户ID',
    content     varchar(255)                          not null comment '弹幕内容',
    color       varchar(20) default '#FFFFFF'         null comment '弹幕颜色，默认白色',
    video_time  decimal(10, 2)                        not null comment '视频播放时间（秒）',
    create_time datetime    default CURRENT_TIMESTAMP null comment '发送时间'
    )
    comment '视频弹幕表' charset = utf8mb4;

create index idx_video_id
    on fotoflow.barrage (video_id);

create table if not exists fotoflow.chat_message
(
    id           bigint auto_increment comment '消息ID'
    primary key,
    from_user_id bigint                             not null comment '发送人ID',
    to_user_id   bigint                             not null comment '接收人ID',
    content      varchar(512)                       not null comment '消息内容',
    is_read      tinyint  default 0                 null comment '0未读 1已读',
    create_time  datetime default CURRENT_TIMESTAMP null comment '发送时间',
    isRead       tinyint  default 0                 not null comment '是否已读 0未读 1已读'
    )
    comment '单表私信消息表';

create index idx_from_to
    on fotoflow.chat_message (from_user_id, to_user_id);

create index idx_to_from
    on fotoflow.chat_message (to_user_id, from_user_id);


create table if not exists fotoflow.comment
(
    id          bigint auto_increment comment '评论主键ID'
    primary key,
    work_id     bigint                             not null comment '关联作品ID',
    user_id     bigint                             not null comment '评论人ID',
    content     varchar(500)                       not null comment '评论内容',
    create_time datetime default CURRENT_TIMESTAMP null comment '评论时间',
    update_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间'
    )
    comment '作品评论表' charset = utf8mb4;

create index idx_user_id
    on fotoflow.comment (user_id);

create index idx_work_id
    on fotoflow.comment (work_id);

create table if not exists fotoflow.user
(
    id           bigint auto_increment comment '主键ID'
    primary key,
    username     varchar(20)                            not null comment '姓名/用户名',
    password     varchar(100)                           not null comment '密码（BCrypt加密）',
    phone        varchar(11)                            null comment '手机号',
    is_real_name tinyint(1)   default 0                 null comment '是否实名认证：0未认证 1已认证',
    role         varchar(10)  default 'user'            null comment '权限：user普通用户 admin管理员',
    create_time  datetime     default CURRENT_TIMESTAMP null comment '创建时间',
    update_time  datetime     default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    real_name    varchar(20)                            null comment '真实姓名',
    id_card      varchar(18)                            null comment '身份证号',
    avatar_url   varchar(255) default ''                null comment '用户头像OSS链接',
    status       tinyint      default 0                 null comment '0正常 1封号',
    ban_reason   varchar(255)                           null comment '账号封禁原因',
    fans_count   int          default 0                 null comment '粉丝数',
    constraint uk_phone
    unique (phone) comment '手机号唯一'
    )
    comment '用户表' charset = utf8mb4;

create table if not exists fotoflow.user_video_collect
(
    id          bigint auto_increment
    primary key,
    user_id     bigint                             not null comment '用户ID',
    video_id    bigint                             not null comment '视频ID',
    create_time datetime default CURRENT_TIMESTAMP null,
    constraint user_video_unique
    unique (user_id, video_id)
    )
    charset = utf8mb4;

create table if not exists fotoflow.user_video_like
(
    id          bigint auto_increment
    primary key,
    user_id     bigint                             not null comment '用户ID',
    video_id    bigint                             not null comment '视频ID',
    create_time datetime default CURRENT_TIMESTAMP null,
    constraint user_video_unique
    unique (user_id, video_id)
    )
    charset = utf8mb4;
create table if not exists fotoflow.video
(
    id            bigint auto_increment
    primary key,
    user_id       bigint                             not null,
    title         varchar(255)                       null,
    content       varchar(500)                       null,
    video_url     varchar(500)                       not null,
    cover_url     varchar(500)                       null,
    duration      int                                null,
    state         int      default 0                 null,
    reject_reason varchar(255)                       null,
    create_time   datetime default CURRENT_TIMESTAMP null,
    update_time   datetime default CURRENT_TIMESTAMP null,
    like_count    bigint   default 0                 null comment '点赞数',
    collect_count bigint   default 0                 null comment '收藏数',
    comment_count bigint   default 0                 null comment '评论数'
    );

create index idx_user_id
    on fotoflow.video (user_id);
create table if not exists fotoflow.video_comment
(
    id          bigint auto_increment
    primary key,
    user_id     bigint                             not null comment '评论用户',
    video_id    bigint                             not null comment '视频ID',
    content     varchar(500)                       not null comment '评论内容',
    create_time datetime default CURRENT_TIMESTAMP null
    )
    charset = utf8mb4;
