package de.KIS.networkinfo;

public class Entry {
	private long id;
	private int cid;
	private int lac;
	private String nType;
	private String network;
	private String time;


	public long getId() 
	{
		return id;
	}

	public void setId(long id) 
	{
		this.id = id;
	}

	public int getCellID() 
	{
		return cid;
	}

	public void setCellID(int cid) 
	{
		this.cid = cid;
	}

	public int getLAC() 
	{
		return lac;
	}

	public void setLAC(int lac) 
	{
		this.lac = lac;
	}

	public String getnType() 
	{
		return nType;
	}

	public void setnType(String nType)
	{
		this.nType = nType;
	}

	public String getNetwork() 
	{
		return network;
	}

	public void setNetwork(String network) 
	{
		this.network = network;
	}

	public String getTime() 
	{
		return network;
	}

	public void setTime(String time) 
	{
		this.time = time;
	}

	@Override
	public String toString() 
	{
		return String.format("Cell ID: %d\nLAC: %d\nNetwork Type: %s\nNetwork: %s\nTime: %s" , cid, lac, nType, network, time);
	}
}