package com.ff.service.impl;

import com.ff.constant.MessageConstant;
import com.ff.constant.OrderStatusConstant;
import com.ff.context.BaseContext;
import com.ff.dto.*;
import com.ff.entity.Goods;
import com.ff.entity.OrderItem;
import com.ff.entity.Orders;
import com.ff.entity.ShopConfig;
import com.ff.entity.UserAddress;
import com.ff.exception.EmptyException;
import com.ff.mapper.GoodsMapper;
import com.ff.mapper.OrderItemMapper;
import com.ff.mapper.OrderMapper;
import com.ff.mapper.ShopMapper;
import com.ff.mapper.UserAddressMapper;
import com.ff.result.PageResult;
import com.ff.service.GoodsService;
import com.ff.vo.MyOrdersVO;
import com.ff.vo.OrderItemVO;
import com.ff.vo.ShopConfigShowConfigVO;
import com.ff.vo.UserAddressVO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

@Service
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private ShopMapper shopMapper;

    @Autowired
    private UserAddressMapper userAddressMapper;

    @Override
    public PageResult selectGoodsRandom(GoodsPageDto goodsPageDto) {
        PageHelper.startPage(goodsPageDto.getPage(), goodsPageDto.getPageSize());
        Page<Goods> goodsPage = goodsMapper.selectGoodsRandom(goodsPageDto);
        return new PageResult(goodsPage.getTotal(), goodsPage.getResult());
    }

    @Override
    public void updateShopConfig(ShopConfigDto shopConfigDto) {
        ShopConfig shopConfig = ShopConfig.builder()
                .id(shopConfigDto.getId())
                .shopName(shopConfigDto.getShopName())
                .shopAddress(shopConfigDto.getShopAddress())
                .longitude(shopConfigDto.getLongitude())
                .latitude(shopConfigDto.getLatitude())
                .updateTime(LocalDateTime.now())
                .build();
        goodsMapper.updateShopConfig(shopConfig);
    }

    @Override
    public void addGoods(GoodsAddDto goodsAddDto) {
        Goods goods = Goods.builder()
                .videoId(goodsAddDto.getVideoId())
                .goodsName(goodsAddDto.getGoodsName())
                .goodsImg(goodsAddDto.getGoodsImg())
                .price(goodsAddDto.getPrice())
                .unit(goodsAddDto.getUnit())
                .content(goodsAddDto.getContent())
                .stock(goodsAddDto.getStock() == null ? 0 : goodsAddDto.getStock())
                .status(1)
                .createTime(LocalDateTime.now())
                .goodsConfigId(goodsAddDto.getGoodsConfigId())
                .build();
        goodsMapper.insert(goods);
    }

    @Override
    public OrderItemVO addMyGoods(Long id) {
        return addMyGoods(id, null);
    }

    @Override
    public OrderItemVO addMyGoods(Long id, Long addressId) {
        Long userId = BaseContext.getCurrentId();
        Goods goods = goodsMapper.selectByPrimaryKey(id);
        if (goods == null) {
            throw new EmptyException(MessageConstant.GOODS_NOT_EXIST);
        }

        OrderItemVO orderItemVO = new OrderItemVO();
        String orderNo = "order:" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
        BeanUtils.copyProperties(goods, orderItemVO);

        orderItemVO.setOrderNo(orderNo);
        orderItemVO.setUserId(userId);
        orderItemVO.setGoodsId(goods.getId());
        orderItemVO.setCount(1);
        orderItemVO.setTotalAmount(goods.getPrice());
        orderItemVO.setStatus(OrderStatusConstant.PENDING_PAY);
        orderItemVO.setGoodsConfigId(goods.getGoodsConfigId());
        orderItemVO.setCreateTime(LocalDateTime.now());

        fillAddress(orderItemVO, userId, addressId);

        ShopConfig shopConfig = goodsMapper.selectShopConfig(goods.getGoodsConfigId());
        if (shopConfig != null) {
            orderItemVO.setShopLongitude(shopConfig.getLongitude());
            orderItemVO.setShopLatitude(shopConfig.getLatitude());
        }
        return orderItemVO;
    }

    private void fillAddress(OrderItemVO orderItemVO, Long userId, Long addressId) {
        UserAddress address = null;
        if (addressId != null) {
            address = userAddressMapper.selectByIdAndUserId(addressId, userId);
            if (address == null) {
                throw new EmptyException(MessageConstant.USER_ADDRESS_NOT_EXIST);
            }
        } else {
            address = userAddressMapper.selectDefaultByUserId(userId);
        }
        if (address != null) {
            orderItemVO.setAddressDetail(address.getAddressDetail());
            orderItemVO.setPhone(address.getPhone());
            orderItemVO.setReceiver(address.getReceiver());
            orderItemVO.setBuyerLongitude(address.getLongitude());
            orderItemVO.setBuyerLatitude(address.getLatitude());
        } else {
            orderItemVO.setAddressDetail("");
            orderItemVO.setPhone("");
            orderItemVO.setReceiver("");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderItemVO SetOrder(OrderItemDto orderItemDto) {
        Long userId = BaseContext.getCurrentId();
        if (!userId.equals(orderItemDto.getUserId())) {
            throw new EmptyException(MessageConstant.ORDER_NO_PERMISSION);
        }

        int count = orderItemDto.getCount() == null ? 1 : orderItemDto.getCount();
        Goods goods = goodsMapper.selectByPrimaryKey(orderItemDto.getGoodsId());
        if (goods == null) {
            throw new EmptyException(MessageConstant.GOODS_NOT_EXIST);
        }
        if (goods.getStock() != null && goods.getStock() < count) {
            throw new EmptyException(MessageConstant.STOCK_NOT_ENOUGH);
        }

        int affected = goodsMapper.decreaseStock(goods.getId(), count);
        if (affected == 0) {
            throw new EmptyException(MessageConstant.STOCK_NOT_ENOUGH);
        }

        Orders orders = Orders.builder()
                .orderNo(orderItemDto.getOrderNo())
                .userId(orderItemDto.getUserId())
                .totalAmount(orderItemDto.getTotalAmount())
                .addressDetail(orderItemDto.getAddressDetail())
                .phone(orderItemDto.getPhone())
                .receiver(orderItemDto.getReceiver())
                .buyerLongitude(orderItemDto.getBuyerLongitude())
                .buyerLatitude(orderItemDto.getBuyerLatitude())
                .shopLongitude(orderItemDto.getShopLongitude())
                .shopLatitude(orderItemDto.getShopLatitude())
                .shopConfigId(orderItemDto.getGoodsConfigId())
                .createTime(LocalDateTime.now())
                .payTime(LocalDateTime.now())
                .status(OrderStatusConstant.PAID_PENDING_ACCEPT)
                .build();

        orderMapper.insert(orders);

        OrderItem orderItem = OrderItem.builder()
                .orderNo(orderItemDto.getOrderNo())
                .goodsId(goods.getId())
                .goodsName(goods.getGoodsName())
                .goodsImg(goods.getGoodsImg())
                .price(goods.getPrice())
                .count(count)
                .createTime(LocalDateTime.now())
                .build();
        orderItemMapper.insert(orderItem);

        OrderItemVO orderItemVO = new OrderItemVO();
        BeanUtils.copyProperties(orders, orderItemVO);
        orderItemVO.setGoodsConfigId(orders.getShopConfigId());
        orderItemVO.setGoodsId(goods.getId());
        orderItemVO.setCount(count);
        return orderItemVO;
    }

    @Override
    public Long addShopConfig(ShopConfigDto dto) {
        Long userId = BaseContext.getCurrentId();
        ShopConfig config = ShopConfig.builder()
                .shopName(dto.getShopName())
                .shopAddress(dto.getShopAddress())
                .longitude(dto.getLongitude())
                .latitude(dto.getLatitude())
                .updateTime(LocalDateTime.now())
                .userId(userId)
                .build();
        goodsMapper.insertShopConfig(config);
        return config.getId();
    }

    @Override
    public List<ShopConfigShowConfigVO> showShopsConfig() {
        Long userId = BaseContext.getCurrentId();
        return shopMapper.selectShopConfigByUserId(userId);
    }

    @Override
    public PageResult orderAll(OrdersPageDto ordersPageDto) {
        Long userId = BaseContext.getCurrentId();
        List<Long> shopConfigIds = shopMapper.selectShopConfigIdsByUserId(userId);
        if (shopConfigIds == null || shopConfigIds.isEmpty()) {
            return new PageResult(0L, Collections.emptyList());
        }
        PageHelper.startPage(ordersPageDto.getPage(), ordersPageDto.getPageSize());
        Page<MyOrdersVO> page = orderMapper.selectByShopConfigIds(shopConfigIds);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public void acceptOrder(Long id) {
        assertMerchantOrder(id);
        orderMapper.acceptOrder(id);
    }

    @Override
    public void cancelOrder(Long id) {
        assertMerchantOrder(id);
        orderMapper.cancelOrder(id);
    }

    @Override
    public void completeOrder(Long id) {
        assertMerchantOrder(id);
        orderMapper.completeOrder(id);
    }

    @Override
    public void deleteOrder(Long id) {
        assertMerchantOrder(id);
        orderMapper.deleteOrder(id);
    }

    @Override
    public PageResult MyOrder(OrdersPageDto ordersPageDto) {
        Long userId = BaseContext.getCurrentId();
        PageHelper.startPage(ordersPageDto.getPage(), ordersPageDto.getPageSize());
        Page<MyOrdersVO> ordersPage = orderMapper.selectMyOrder(ordersPageDto, userId);
        return new PageResult(ordersPage.getTotal(), ordersPage.getResult());
    }

    @Override
    public MyOrdersVO myOrderDetail(Long id) {
        Long userId = BaseContext.getCurrentId();
        Orders orders = orderMapper.selectById(id);
        if (orders == null) {
            throw new EmptyException(MessageConstant.ORDER_NOT_EXIST);
        }
        if (!userId.equals(orders.getUserId()) && !isMerchantOfOrder(userId, orders)) {
            throw new EmptyException(MessageConstant.ORDER_NO_PERMISSION);
        }
        MyOrdersVO vo = new MyOrdersVO();
        BeanUtils.copyProperties(orders, vo);
        return vo;
    }

    @Override
    public void confirmOrder(Long id) {
        Long userId = BaseContext.getCurrentId();
        int rows = orderMapper.confirmOrder(id, userId);
        if (rows == 0) {
            throw new EmptyException(MessageConstant.ORDER_STATUS_ERROR);
        }
    }

    @Override
    public void cancelMylOrder(Long id) {
        Long userId = BaseContext.getCurrentId();
        int rows = orderMapper.cancelMyOrderByUser(id, userId);
        if (rows == 0) {
            throw new EmptyException(MessageConstant.ORDER_STATUS_ERROR);
        }
    }

    @Override
    public void updateDeliveryLocation(Long id, DeliveryLocationDto dto) {
        assertMerchantOrder(id);
        orderMapper.updateDeliveryLocation(id, dto.getDeliveryLongitude(), dto.getDeliveryLatitude());
    }

    @Override
    public void updateGoods(GoodsUpdateDto goodsUpdateDto) {
        if (goodsUpdateDto.getId() == null) {
            throw new EmptyException(MessageConstant.GOODS_NOT_EXIST);
        }
        goodsMapper.updateGoods(goodsUpdateDto);
    }

    @Override
    public List<UserAddressVO> MyAddress() {
        Long userId = BaseContext.getCurrentId();
        return userAddressMapper.selectMyAddress(userId);
    }

    @Override
    public UserAddressVO MyAddressShow(Long id) {
        Long userId = BaseContext.getCurrentId();
        UserAddress userAddress = userAddressMapper.selectByIdAndUserId(id, userId);
        if (userAddress == null) {
            throw new EmptyException(MessageConstant.USER_ADDRESS_NOT_EXIST);
        }
        UserAddressVO userAddressVO = new UserAddressVO();
        BeanUtils.copyProperties(userAddress, userAddressVO);
        return userAddressVO;
    }

    @Override
    public void MyAddressUpdate(Long id, UserAddressDto userAddressDto) {
        Long userId = BaseContext.getCurrentId();
        UserAddress exist = userAddressMapper.selectByIdAndUserId(id, userId);
        if (exist == null) {
            throw new EmptyException(MessageConstant.USER_ADDRESS_NOT_EXIST);
        }
        userAddressDto.setId(id);
        if (userAddressDto.getIsDefault() != null && userAddressDto.getIsDefault() == 1) {
            UserAddressDto resetDefault = new UserAddressDto();
            resetDefault.setUserId(userId);
            resetDefault.setIsDefault(0);
            userAddressMapper.updateMyAddressDefault(resetDefault);
        }
        userAddressMapper.updateMyAddress(userAddressDto);
    }

    @Override
    public void MyAddressAdd(UserAddressDto userAddressDto) {
        Long userId = BaseContext.getCurrentId();
        userAddressDto.setUserId(userId);
        if (userAddressDto.getIsDefault() == null) {
            userAddressDto.setIsDefault(0);
        }
        if (userAddressDto.getIsDefault() == 1) {
            UserAddressDto resetDefault = new UserAddressDto();
            resetDefault.setUserId(userId);
            resetDefault.setIsDefault(0);
            userAddressMapper.updateMyAddressDefault(resetDefault);
        }
        userAddressMapper.insertMyAddress(userAddressDto);
    }

    @Override
    public void MyAddressDelete(Long id) {
        Long userId = BaseContext.getCurrentId();
        int rows = userAddressMapper.deleteMyAddress(id, userId);
        if (rows == 0) {
            throw new EmptyException(MessageConstant.USER_ADDRESS_NOT_EXIST);
        }
    }

    private void assertMerchantOrder(Long orderId) {
        Long userId = BaseContext.getCurrentId();
        Orders orders = orderMapper.selectById(orderId);
        if (orders == null) {
            throw new EmptyException(MessageConstant.ORDER_NOT_EXIST);
        }
        if (!isMerchantOfOrder(userId, orders)) {
            throw new EmptyException(MessageConstant.ORDER_NO_PERMISSION);
        }
    }

    private boolean isMerchantOfOrder(Long userId, Orders orders) {
        List<Long> shopConfigIds = shopMapper.selectShopConfigIdsByUserId(userId);
        return shopConfigIds != null && shopConfigIds.contains(orders.getShopConfigId());
    }
}
