package models;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * Корінний клас для усіх моделей.
 *
 * Модель дозволяє зберігати певний об'єкт усередині як тег.
 *
 * Created by shrralis on 2/19/17.
 */
public abstract class Model implements Serializable {
    /**
     * The client.model's tag.
     */
    private Object mTag;
    /**
     * Map used to store client.model's tags.
     */
    private HashMap<Integer, Object> mKeyedTags;

    /**
     * Creates empty client.model
     */
    public Model() {}
    /**
     * Returns this client.model's tag.
     *
     * @return the Object stored in this client.model as a tag
     *
     * @see #setTag(Object)
     * @see #getTag(int)
     */
    @SuppressWarnings("unused")
    public Object getTag() {
        return mTag;
    }
    /**
     * Sets the tag associated with this client.model. A tag can be used to store
     * data within a client.model without resorting to another data structure.
     *
     * @param tag an Object to tag the client.model with
     *
     * @see #getTag()
     * @see #setTag(int, Object)
     */
    @SuppressWarnings("unused")
    public void setTag(Object tag) {
        mTag = tag;
    }

    /**
     * Returns the tag associated with this client.model and the specified key.
     *
     * @param key The key identifying the tag
     *
     * @return the Object stored in this client.model as a tag
     *
     * @see #setTag(int, Object)
     * @see #getTag()
     */
    @SuppressWarnings("unused")
    public Object getTag(int key) {
        if (mKeyedTags != null) return mKeyedTags.get(key);
        return null;
    }
    /**
     * Sets a tag associated with this client.model and a key. A tag can be used
     * to store data within a client.model without resorting to another
     * data structure.
     *
     * @see #setTag(Object)
     * @see #getTag(int)
     */
    @SuppressWarnings("unused")
    public void setTag(int key, final Object tag) {
        if (mKeyedTags == null) {
            mKeyedTags = new HashMap<>(2);
        }
        mKeyedTags.put(key, tag);
    }
    @SuppressWarnings("unused")
    public Model(ResultSet from) throws SQLException {
        parse(from);
    }
    /**
     * Parses object from source.
     * @param response server API object.
     * @return this object.
     * @throws SQLException if any critical error occurred while parsing.
     */
    public Model parse(ResultSet response) throws SQLException {
        return ParseUtils.parseViaReflection(this, response);
    }
    /**
     * Parses object from source.
     * @param response server API object.
     * @return this object.
     * @throws SQLException if any critical error occurred while parsing.
     */
    public Model parse(ResultSet response, Connection connection) throws SQLException {
        return ParseUtils.parseViaReflection(this, response);
    }
}
