package network.starplum.interfaces;

import java.util.List;
import java.util.UUID;

public interface ITeam {

    int getTeamId();
    String getTeamName();
    String getTeamTag();
    List<UUID> getMembers();
    UUID getOwner();
    
}
