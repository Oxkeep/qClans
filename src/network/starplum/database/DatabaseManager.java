package network.starplum.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.json.JSONArray;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import network.starplum.interfaces.ITeam;

public class DatabaseManager {

	private final MysqlDataSource dataSource;

	public DatabaseManager(DatabaseCredentials databaseCredentials) {
		this.dataSource = new MysqlDataSource();
		this.dataSource.setUser(databaseCredentials.getUser());
		this.dataSource.setPassword(databaseCredentials.getPassword());
		this.dataSource.setServerName(databaseCredentials.getHost());
		this.dataSource.setPort(databaseCredentials.getPort());
		this.dataSource.setURL("jdbc:mysql://" + databaseCredentials.getHost() + "/" + databaseCredentials.getDatabase() + "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=EST");
	}

	public boolean doesTableExist() {
		try {
			Connection connection = this.dataSource.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement("SELECT 1 FROM clans LIMIT 1;");
			preparedStatement.executeQuery();
			connection.close();
			preparedStatement.close();
			return true;
		} catch (SQLException e) {
			return false;
		}
	}

	public boolean canConnect() {
		try {
			Connection connection = this.dataSource.getConnection();
			connection.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public void createTable() {
		try {
			Connection connection = this.dataSource.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement("CREATE TABLE clans ( clanId INT PRIMARY KEY NOT NULL AUTO_INCREMENT, clanName TEXT, clanTag TEXT, owner TEXT, members TEXT );");
			preparedStatement.executeUpdate();
			preparedStatement.close();
			preparedStatement = connection.prepareStatement("CREATE UNIQUE INDEX clans_clanId_uindex ON clans (clanId);");
			preparedStatement.executeUpdate();
			connection.close();
			preparedStatement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public boolean isClanNameTaken(String clanName) {
		boolean result = false;
		try {
			Connection connection = this.dataSource.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement("SELECT clanId FROM `clans` WHERE clans.clanName = ?;");
			preparedStatement.setString(1, clanName);
			ResultSet resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				result = true;
			}
			connection.close();
			resultSet.close();
			preparedStatement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	public boolean isInATeam(UUID uuid) {
		try {
			Connection connection = this.dataSource.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement("SELECT members FROM `clans`;");
			ResultSet resultSet = preparedStatement.executeQuery();
			List<UUID> results = new ArrayList<>();
			while (resultSet.next()) {
				JSONArray jsonArray = new JSONArray(resultSet.getString(1));
				for (int i = 0; i < jsonArray.length(); i++) {
					results.add(UUID.fromString(jsonArray.getString(i)));
				}
			}
			connection.close();
			resultSet.close();
			preparedStatement.close();
			return results.contains(uuid);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return true;
	}

	public List<ITeam> getClans() {
		List<ITeam> clans = new ArrayList<>();
		try {
			Connection connection = this.dataSource.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement("SELECT clanName, clanTag, members, owner, clanId FROM `clans`;");
			ResultSet resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				String clanName = resultSet.getString(1);
				String clanTag = resultSet.getString(2);
				List<UUID> members = new ArrayList<>();
				JSONArray memberArray = new JSONArray(resultSet.getString(3));
				for (int i = 0; i < memberArray.length(); i++) { members.add(UUID.fromString(memberArray.getString(i))); }
				UUID owner = UUID.fromString(resultSet.getString(4));
				int clanId = resultSet.getInt(5);
				clans.add(new ITeam() {
					
					@Override
					public int getTeamId() {
						return clanId;
					}

					@Override
					public String getTeamName() {
						return clanName;
					}

					@Override
					public String getTeamTag() {
						return clanTag;
					}

					@Override
					public List<UUID> getMembers() {
						return members;
					}

					@Override
					public UUID getOwner() {
						return owner;
					}

				});
			}
			connection.close();
			preparedStatement.close();
			resultSet.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return clans;
	}

	public ITeam getClan(String clanName) {
		try {
			Connection connection = this.dataSource.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement("SELECT clanId, clanTag, members, owner FROM `clans` WHERE  clanName = ?;");
			preparedStatement.setString(1, clanName);
			ResultSet resultSet = preparedStatement.executeQuery();
			if (!resultSet.next()) { return null; }
			int clanId = resultSet.getInt(1);
			String clanTag = resultSet.getString(2);
			List<UUID> members = new ArrayList<>();
			JSONArray memberArray = new JSONArray(resultSet.getString(3));
			for (int i = 0; i < memberArray.length(); i++) { members.add(UUID.fromString(memberArray.getString(i))); }
			UUID owner = UUID.fromString(resultSet.getString(4));
			connection.close();
			resultSet.close();
			preparedStatement.close();
			return new ITeam() {
				@Override
				public int getTeamId() {
					return clanId;
				}

				@Override
				public String getTeamName() {
					return clanName;
				}

				@Override
				public String getTeamTag() {
					return clanTag;
				}

				@Override
				public List<UUID> getMembers() {
					return members;
				}

				@Override
				public UUID getOwner() {
					return owner;
				}

			};
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public ITeam getTeam(int clanId) {
		try {
			Connection connection = this.dataSource.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement("SELECT clanName, clanTag, members, owner FROM `clans` WHERE clanId = ?;");
			preparedStatement.setInt(1, clanId);
			ResultSet resultSet = preparedStatement.executeQuery();
			if (!resultSet.next()) {
				return null;
			}
			String clanName = resultSet.getString(1);
			String clanTag = resultSet.getString(2);
			List<UUID> members = new ArrayList<>();
			JSONArray memberArray = new JSONArray(resultSet.getString(3));
			for (int i = 0; i < memberArray.length(); i++) {
				members.add(UUID.fromString(memberArray.getString(i)));
			}
			UUID owner = UUID.fromString(resultSet.getString(4));
			connection.close();
			resultSet.close();
			preparedStatement.close();
			return new ITeam() {
				@Override
				public int getTeamId() {
					return clanId;
				}

				@Override
				public String getTeamName() {
					return clanName;
				}

				@Override
				public String getTeamTag() {
					return clanTag;
				}

				@Override
				public List<UUID> getMembers() {
					return members;
				}

				@Override
				public UUID getOwner() {
					return owner;
				}

			};
		} catch (SQLException e) { e.printStackTrace(); }
		return null;
	}

	public ITeam getClan(UUID uuid) {
		try {
			Connection connection = this.dataSource.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement("SELECT clanId, members FROM `clans`;");
			ResultSet resultSet = preparedStatement.executeQuery();
			ITeam clan = null;
			loop:
				while (resultSet.next()) {
					JSONArray memberArray = new JSONArray(resultSet.getString(2));
					for (int i = 0; i < memberArray.length(); i++) {
						if (UUID.fromString(memberArray.getString(i)).equals(uuid)) {
							clan = this.getTeam(resultSet.getInt(1));
							break loop;
						}
					}
				}
			connection.close();
			resultSet.close();
			preparedStatement.close();
			return clan;
		} catch (SQLException e) { e.printStackTrace(); }
		return null;
	}

	/**
	 * CLANNAME : 1 <br>
	 * CLANTAG  : 2 <br>
	 * OWNER    : 3 <br>
	 * MEMBERS  : 4 <br>
	 */
	public void createClan(String clanName, String clanTag, UUID owner) {
		try {
			Connection connection = this.dataSource.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO clans(clanName, clanTag, owner, members) VALUES (?, ?, ?, ?);");
			preparedStatement.setString(1, clanName);
			preparedStatement.setString(2, clanTag);
			preparedStatement.setString(3, owner.toString());
			JSONArray jsonArray = new JSONArray();
			jsonArray.put(owner.toString());
			preparedStatement.setString(4, jsonArray.toString());
			preparedStatement.executeUpdate();
			connection.close();
			preparedStatement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void updateMembers(int clanId, List<UUID> members) {
		try {
			Connection connection = this.dataSource.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement("UPDATE clans SET members= ? WHERE clanId = ?;");
			JSONArray jsonArray = new JSONArray();
			members.forEach(member -> jsonArray.put(member.toString()));
			preparedStatement.setString(1, jsonArray.toString());
			preparedStatement.setInt(2, clanId);
			preparedStatement.executeUpdate();
			connection.close();
			preparedStatement.close();
		} catch (SQLException e) { e.printStackTrace(); }
	}

	public void deleteClan(int clanId) {
		try {
			Connection connection = this.dataSource.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM clans WHERE clanId = ?;");
			preparedStatement.setInt(1, clanId);
			preparedStatement.executeUpdate();
			connection.close();
			preparedStatement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}