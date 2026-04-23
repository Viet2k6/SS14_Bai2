package org.example.ss14_bai2.service;

import org.example.ss14_bai2.model.Order;
import org.example.ss14_bai2.model.Product;
import org.example.ss14_bai2.util.HibernateUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Service;

@Service
public class CancelOrderService {

    public void cancelOrder(Long orderId) {
        Session session = HibernateUtils.getSessionFactory().openSession();
        Transaction tx = null;

        try {
            tx = session.beginTransaction();
            Order order = session.get(Order.class, orderId);
            if (order == null) {
                throw new RuntimeException("Đơn hàng không tồn tại!");
            }

            order.setStatus("CANCELLED");
            session.update(order);

            Product product = session.get(Product.class, order.getProductId());
            if (product == null) {
                throw new RuntimeException("Sản phẩm không tồn tại!");
            }

            product.setStock(product.getStock() + order.getQuantity());
            session.update(product);

            tx.commit();

        } catch (Exception e) {
            System.out.println("Lỗi: " + e.getMessage());

            if (tx != null) {
                tx.rollback();
            }
        } finally {
            session.close();
        }
    }
}