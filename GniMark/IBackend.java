import java.util.Vector;


public interface IBackend 
{
	public Vector<String> quickRecommend(String url, int offset, int count);
	public Vector<String> slowRecommend(String url, int offset, int count);
	public Vector<String> quickRecommend(String url, String username, int offset, int count);
	public Vector<String> slowRecommend(String url, String username, int offset, int count);
}
