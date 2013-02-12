package jp.co.flect.cache;

/**
 * 標準キャッシュInterface
 */
public interface Cache {
	
	/**
	 * タイムアウト指定なしでオブジェクトをセットします。<br>
	 * この場合タイムアウトするか否かは実装依存です。
	 */
	public void set(String key, Object value);
	
	/**
	 * タイムアウト指定でオブジェクトをセットします。
	 */
	public void set(String key, Object value, int expiration);
	
	/**
	 * オブジェクトを取得します。
	 */
	public Object get(String key);
	/**
	 * オブジェクトを削除します。
	 */
	public void delete(String key);
	
	/**
	 * キャッシュをクリアします。
	 */
	public void clear();
}
