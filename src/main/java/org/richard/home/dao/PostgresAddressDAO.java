package org.richard.home.dao;

import org.richard.home.exception.DatabaseAccessFailed;
import org.richard.home.exception.NotFoundException;
import org.richard.home.model.Address;
import org.richard.home.model.Country;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class PostgresAddressDAO implements AddressDAO {

    Logger log = LoggerFactory.getLogger(PostgresAddressDAO.class);

    private DataSource readDataSource;
    private DataSource writeDataSource;

    private static String FIND_ADDRESS_BY_ID = "SELECT * FROM ADDRESSES WHERE ID = ?";
    private static String SAVE_ADDRESS = "INSERT INTO ADDRESSES VALUES (default, ?, ?, ?, ?)";

    @Autowired
    public PostgresAddressDAO(@Qualifier("hikariDataSource") DataSource readOnly, @Qualifier("readDataSource") DataSource writeOnly) {
        this.readDataSource = readOnly;
        this.writeDataSource = writeOnly;
    }

    @Override
    public Address getAddress(long id) throws DatabaseAccessFailed {
        try (Connection con = readDataSource.getConnection()){
            try (PreparedStatement pS = con.prepareStatement(FIND_ADDRESS_BY_ID, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)){
                pS.setLong(1, id);
                ResultSet rs = pS.executeQuery();
                return mapResultSetToAddress(rs, id);
            }
        } catch (SQLException e) {
            log.error("error while getting the address", e);
            throw new DatabaseAccessFailed("error while getting the address", e);
        } catch (NotFoundException e){
            log.error("error while getting the address", e);
            throw new NotFoundException(String.format("error while getting the address with id %d", id), e);
        }
    }

    @Override
    public boolean saveAddress(Address toSave) throws DatabaseAccessFailed {
        int updtRows = 0;
        try (Connection con = writeDataSource.getConnection()){
            try (PreparedStatement pS = con.prepareStatement(SAVE_ADDRESS, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)){
                pS.setString(1, toSave.getCity());
                pS.setString(2, toSave.getStreet());
                pS.setString(3, toSave.getPlz());
                pS.setString(4, toSave.getCountry().toString());
                updtRows = pS.executeUpdate();
            }
        } catch (SQLException e) {
            log.error("error while saving address");
            throw new DatabaseAccessFailed("error while saving address", e);
        }
        return updtRows > 0;
    }

    @Override
    public boolean updateAddress(Address toBe, long whereId) throws DatabaseAccessFailed {
        return false;
    }

    @Override
    public boolean deleteAddress(long whereId) throws DatabaseAccessFailed {
        return false;
    }

    private Address mapResultSetToAddress(ResultSet rs, long id) throws SQLException {
        if (!rs.next()) {
            throw new NotFoundException(String.format("player with name %s not found!", id));
        }
        return new Address(rs.getLong(1), rs.getString(2), rs.getString(3),
                rs.getString(4), Country.valueOf(rs.getString(5)));
    }
}
