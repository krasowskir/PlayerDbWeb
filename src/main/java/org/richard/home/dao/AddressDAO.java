package org.richard.home.dao;

import org.richard.home.exception.DatabaseAccessFailed;
import org.richard.home.model.Address;

public interface AddressDAO {

    Address getAddress(long id) throws DatabaseAccessFailed;

    boolean saveAddress(Address toSave) throws DatabaseAccessFailed;

    boolean updateAddress(Address toBe, long whereId) throws DatabaseAccessFailed;

    boolean deleteAddress(long whereId) throws DatabaseAccessFailed;
}
