package network.starplum.config;

public interface IConfig {

	String getDbHost();
	String getDbUser();
	String getDbPassword();
	String getDbDb();
	int getDbPort();
	boolean setClanTagTabPrefix();
	boolean flushScoreboardOnJoin();

}