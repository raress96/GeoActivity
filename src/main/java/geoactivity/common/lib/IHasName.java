package geoactivity.common.lib;

public interface IHasName
{
	public String getName();

	public default String getName(int meta)
	{
		return this.getName();
	}
}
