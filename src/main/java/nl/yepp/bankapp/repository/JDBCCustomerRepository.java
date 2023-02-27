package nl.yepp.bankapp.repository;

import nl.yepp.bankapp.model.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
@Primary
public class JDBCCustomerRepository implements CustomerRepository {

    // TODO
    // 1. Pagination

    @Autowired
    JdbcTemplate jdbcTemplate;
    @Override
    public Customer save(Customer customer) {
        final String updateSql = "INSERT INTO CUSTOMER VALUES (?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(updateSql);
                ps.setString(1, customer.getEmail());
                ps.setString(2, customer.getName());
                return ps;
            }
        }, keyHolder);
        customer.setId((long) keyHolder.getKey());
        return customer;
    }

    @Override
    public Optional<Customer> findById(Long customerId) {
        return Optional.of(jdbcTemplate.queryForObject(
                "SELECT * FROM CUSTOMER WHERE ID = ?", new Object[] { customerId }, new CustomerMapper()));
    }

    @Override
    public void deleteById(Long customerId) {
        jdbcTemplate.update("DELETE FROM CUSTOMER WHERE ID = ?", customerId);
    }

    @Override
    public List<Customer> findAll() {
        return jdbcTemplate.queryForList(
                "SELECT * FROM CUSTOMER", Customer.class);
    }
}

class CustomerMapper implements RowMapper<Customer> {
    @Override
    public Customer mapRow(ResultSet rs, int rowNum) throws SQLException {
        Customer customer = new Customer();

        customer.setId(rs.getLong("ID"));
        customer.setEmail(rs.getString("EMAIL"));
        customer.setName(rs.getString("NAME"));

        return customer;
    }
}
