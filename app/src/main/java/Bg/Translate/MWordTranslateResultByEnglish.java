package Bg.Translate;

import java.util.List;
import java.util.Map;

import Model.WordInfoItem;
import Model.WordInstance;

public class MWordTranslateResultByEnglish
{
	//是否成功
	public boolean success;
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

	public WordInfoItem toWordInfoItem()
	{
		return new WordInfoItem(english, translation, soundmark, imgs, instances, similarity);
	}
	public MWordTranslateResultByEnglish(boolean success, String english,
										 Map<String, String> translation, Map<String, String> soundmark,
										 List<String> imgs, List<WordInstance> instances,
										 List<String> similarity)
	{
		this.success = success;
		this.english = english;
		this.translation = translation;
		this.soundmark = soundmark;
		this.imgs = imgs;
		this.instances = instances;
		this.similarity = similarity;
	}
	public boolean getSuccess()
	{
		return success;
	}
	public void setSuccess(boolean success)
	{
		this.success = success;
	}
	public String getEnglish()
	{
		return english;
	}
	public void setEnglish(String english)
	{
		this.english = english;
	}
	public Map<String, String> getTranslation()
	{
		return translation;
	}
	public void setTranslation(Map<String, String> translation)
	{
		this.translation = translation;
	}
	public Map<String, String> getSoundmark()
	{
		return soundmark;
	}
	public void setSoundmark(Map<String, String> soundmark)
	{
		this.soundmark = soundmark;
	}
	public List<String> getImgs()
	{
		return imgs;
	}
	public void setImgs(List<String> imgs)
	{
		this.imgs = imgs;
	}
	public List<WordInstance> getInstances()
	{
		return instances;
	}
	public void setInstances(List<WordInstance> instances)
	{
		this.instances = instances;
	}
	public List<String> getSimilarity()
	{
		return similarity;
	}
	public void setSimilarity(List<String> similarity)
	{
		this.similarity = similarity;
	}


}
