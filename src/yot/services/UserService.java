package yot.services;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;
import java.util.Random;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.swing.JOptionPane;

public class UserService {
	private static final Random RANDOM = new SecureRandom();
	private static final Base64.Encoder enc = Base64.getEncoder();
	private static final Base64.Decoder dec = Base64.getDecoder();
	private DatabaseConnectionService dbService = null;

	public UserService(DatabaseConnectionService dbService) {
		this.dbService = dbService;
	}

	public boolean useApplicationLogins() {
		return true;
	}
	
	public boolean login(String username, String password) {
		try {
			Connection connection = this.dbService.getConnection();
			CallableStatement stmt = connection.prepareCall("{CALL GetCredentials(?)}");
			stmt.setString(1, username);
			ResultSet rs = stmt.executeQuery();
			rs.next();
			byte[] salt = rs.getBytes("PasswordSalt");
			String hash = rs.getString("PasswordHash");
			if(hashPassword(salt, password).equals(hash)) return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean register(String username, String password) {
		try {
			byte[] salt = getNewSalt();
			String hash = hashPassword(salt, password);
			Connection connection = this.dbService.getConnection();
			CallableStatement stmt = connection.prepareCall("{CALL Register(?, ?, ?)}");
			stmt.setString(1, username);
			stmt.setBytes(2, salt);
			stmt.setString(3, hash);
			stmt.execute();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * Returns whether the user is registered as a seller.
	 * Requires stored procedure: {@code IsSeller(@Username)} returning a single row with a non-zero int / bit meaning true.
	 */
	public boolean isSeller(String username) {
		try {
			CallableStatement stmt = dbService.getConnection().prepareCall("{call IsSeller(?)}");
			stmt.setString(1, username);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				return rs.getInt(1) != 0;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean registerSeller(String username, String password, String phone, String storeName, String address, String city, String state, String zipcode, String desc) {
		try {
			if(!register(username, password)) {
				if(!login(username, password)) {
					System.out.println("invalid username or password");
					return false;
				}
			}
			Connection connection = this.dbService.getConnection();
			CallableStatement stmt = connection.prepareCall("{CALL RegisterSeller(?, ?, ?, ?, ?, ?, ?, ?)}");
			stmt.setString(1, username);
			stmt.setString(2, phone);
			stmt.setString(3, storeName);
			stmt.setString(4, address);
			stmt.setString(5, city);
			stmt.setString(6, state);
			stmt.setString(7, zipcode);
			stmt.setString(8, desc);
			stmt.execute();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public byte[] getNewSalt() {
		byte[] salt = new byte[16];
		RANDOM.nextBytes(salt);
		return salt;
	}
	
	public String getStringFromBytes(byte[] data) {
		return enc.encodeToString(data);
	}

	public String hashPassword(byte[] salt, String password) {

		KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128);
		SecretKeyFactory f;
		byte[] hash = null;
		try {
			f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			hash = f.generateSecret(spec).getEncoded();
		} catch (NoSuchAlgorithmException e) {
			JOptionPane.showMessageDialog(null, "An error occurred during password hashing. See stack trace.");
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			JOptionPane.showMessageDialog(null, "An error occurred during password hashing. See stack trace.");
			e.printStackTrace();
		}
		return getStringFromBytes(hash);
	}

}
