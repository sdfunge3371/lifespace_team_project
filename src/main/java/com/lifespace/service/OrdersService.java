package com.lifespace.service;


import com.lifespace.dto.OrdersDTO;
import com.lifespace.entity.Orders;
import com.lifespace.repository.OrdersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.lifespace.mapper.OrdersMapper;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service("ordersService")
public class OrdersService {

    @Autowired
    private OrdersRepository ordersRepository;

    public void updateOrderStatusByOrderId(String orderId) {
        ordersRepository.updateOrderStatusByOrderId(0, orderId);
    }

    public Orders getOneOrder(String orderId) {

        Optional<Orders> optional = ordersRepository.findById(orderId);
        return optional.orElse(null);
    }

    public List<Orders> getAllOrders() {
        return ordersRepository.findAll();
    }

    public List<OrdersDTO> getAllOrdersDTOs() {

        return getAllOrders().stream()
                .map(OrdersMapper::toOrdersDTO)
                .collect(Collectors.toList());
    }


}
