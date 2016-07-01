package Model;

import java.util.List;
import java.util.Map;

public class WordInfoItem extends WordItem
{
	private static final long serialVersionUID = 1L;

	//英语
	public String english;
	//翻译
	public Map<String,String> translation;
	//音标
	public Map<String, String> soundmark;
	//例图
	public List<String> imgs;
	//例句
	public List<WordInstance> instances;
	//相似单词
	public List<String> similarity;

	public WordInfoItem(String english, Map<String, String> translation,
						Map<String, String> soundmark, List<String> imgs,
						List<WordInstance> instances, List<String> similarity)
	{
		super(-1,english,translation.toString());
		this.english = english;
		this.translation = translation;
		this.soundmark = soundmark;
		this.imgs = imgs;
		this.instances = instances;
		this.similarity = similarity;
	}

	public String getenglish()
	{
		return english;
	}

	public void setenglish(String english)
	{
		this.english = english;
	}

	public Map<String, String> gettranslation()
	{
		return translation;
	}

	public void settranslation(Map<String, String> translation)
	{
		this.translation = translation;
	}

	public Map<String, String> getsoundmark()
	{
		return soundmark;
	}

	public void setsoundmark(Map<String, String> soundmark)
	{
		this.soundmark = soundmark;
	}

	public List<String> getimgs()
	{
		return imgs;
	}

	public void setimgs(List<String> imgs)
	{
		this.imgs = imgs;
	}

	public List<WordInstance> getinstances()
	{
		return instances;
	}

	public void setinstances(List<WordInstance> instances)
	{
		this.instances = instances;
	}

	public List<String> getsimilarity()
	{
		return similarity;
	}

	public void setsimilarity(List<String> similarity)
	{
		this.similarity = similarity;
	}



}
