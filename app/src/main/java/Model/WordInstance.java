package Model;


//单词例句
public class WordInstance
{
	//例句的英文
	public String en;
	//例句的中文
	public String cn;
	public WordInstance(String en, String cn)
	{
		super();
		this.en = en;
		this.cn = cn;
	}
	public String getEn()
	{
		return en;
	}
	public void setEn(String en)
	{
		this.en = en;
	}
	public String getCn()
	{
		return cn;
	}
	public void setCn(String cn)
	{
		this.cn = cn;
	}


}
