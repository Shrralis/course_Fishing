package models;

import com.sun.istack.internal.NotNull;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.regex.Pattern;

/**
 * Created by shrralis on 2/22/17.
 */
@SuppressWarnings({"unchecked", "UnusedDeclaration"})
public class List<T extends Owner> extends Model implements java.util.List<T>, Serializable {
    private final static int NO_COUNT = -1;

    private ArrayList<T> items = new ArrayList<>();

    private int count = NO_COUNT;

    public List() {}

    public List(java.util.List<? extends T> data) {
        assert data != null;
        items = new ArrayList<>(data);
    }

    public List(ResultSet from, Class<? extends T> clazz) {
        fill(from, clazz);
    }

    public List(ResultSet from, Class<? extends T> clazz, Connection connection) {
        fill(from, clazz, connection);
    }

    public List(ResultSet from, Parser<T> creator) {
        fill(from, creator);
    }

    public void fill(ResultSet from, Class<? extends T> clazz) {
        if (from != null) {
            fill(from, new ReflectParser<>(clazz));
        }
    }

    public void fill(ResultSet from, Class<? extends T> clazz, Connection connection) {
        if (from != null) {
            fill(from, new ReflectParser<>(clazz), connection);
        }
    }

    public void fill(ResultSet from, Parser<? extends T> creator, Connection connection) {
        if (from != null) {
            try {
                from.beforeFirst();

                while (from.next()) {
                    T object = creator.parseObject(from, connection);

                    if (object != null) {
                        items.add(object);
                    }
                }
            } catch (Exception ignored) {}
        }
    }

    public void fill(ResultSet from, Parser<? extends T> creator) {
        if (from != null) {
            try {
                from.beforeFirst();

                while (from.next()) {
                    T object = creator.parseObject(from);

                    if (object != null) {
                        items.add(object);
                    }
                }
            } catch (Exception ignored) {}
        }
    }

    public void addBefore(int id, T data) {
        int size = size();

        for(int i = 0; i < size; i++)
            if(get(i).getId() > id || i == size - 1) {
                add(i, data);
                break;
            }
    }

    public void addAfter(int id, T data) {
        int size = size();

        for(int i = 0; i < size; i++)
            if(get(i).getId() > id || i == size - 1) {
                add(i + 1, data);
                break;
            }
    }

    public T getById(int id) {
        for (T item : this) {
            if (item.getId() == id) {
                return item;
            }
        }
        return null;
    }

    public List<T> search(String query) {
        List<T> result = new List<>();
        final Pattern pattern = Pattern.compile("(?i).*\\b" + query + ".*");

        for (T item : this) {
            if (pattern.matcher(item.toString()).find()) {
                result.add(item);
            }
        }
        return result;
    }

    public int getCount() {
        return count != NO_COUNT ? count : size();
    }

    @Override
    public void add(int location, T object) {
        items.add(location, object);
    }

    @Override
    public boolean add(T object) {
        return items.add(object);
    }

    @Override
    public boolean addAll(int location, @NotNull Collection<? extends T> collection) {
        return items.addAll(location, collection);
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends T> collection) {
        return items.addAll(collection);
    }

    @Override
    public void clear() {
        items.clear();
    }

    @Override
    public boolean contains(Object object) {
        return items.contains(object);
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> collection) {
        return items.containsAll(collection);
    }

    @Override
    public boolean equals(Object object) {
        return ((Object) this).getClass().equals(object.getClass()) && items.equals(object);
    }

    @Override
    public T get(int location) {
        return items.get(location);
    }

    @Override
    public int indexOf(Object object) {
        return items.indexOf(object);
    }

    @Override
    public boolean isEmpty() {
        return items.isEmpty();
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return items.iterator();
    }

    @Override
    public int lastIndexOf(Object object) {
        return items.lastIndexOf(object);
    }


    @Override
    public ListIterator<T> listIterator() {
        return items.listIterator();
    }

    @NotNull
    @Override
    public ListIterator<T> listIterator(int location) {
        return items.listIterator(location);
    }

    @Override
    public T remove(int location) {
        return items.remove(location);
    }

    @Override
    public boolean remove(Object object) {
        return items.remove(object);
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> collection) {
        return items.removeAll(collection);
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> collection) {
        return items.retainAll(collection);
    }

    @Override
    public T set(int location, T object) {
        return items.set(location, object);
    }

    @Override
    public int size() {
        return items.size();
    }

    @NotNull
    @Override
    public java.util.List<T> subList(int start, int end) {
        return items.subList(start, end);
    }

    @NotNull
    @Override
    public Object[] toArray() {
        return items.toArray();
    }

    @NotNull
    @Override
    public <T1> T1[] toArray(@NotNull T1[] array) {
        return items.toArray(array);
    }

    public interface Parser<D> {
        D parseObject(ResultSet source) throws Exception;
        D parseObject(ResultSet source, Connection connection) throws Exception;
    }

    public final static class ReflectParser<D extends Model> implements Parser<D> {

        private final Class<? extends D> clazz;

        public ReflectParser(Class<? extends D> clazz) {
            this.clazz = clazz;
        }
        @Override
        public D parseObject(ResultSet source) throws Exception {
            try {
                Constructor<? extends D> resultSetConstructor = clazz.getConstructor(ResultSet.class);

                if (resultSetConstructor != null) {
                    return resultSetConstructor.newInstance(source);
                }
            } catch (Exception ignored) {}
            return (D) clazz.newInstance().parse(source);
        }
        @Override
        public D parseObject(ResultSet source, Connection connection) throws Exception {
            try {
                Constructor<? extends D> resultSetConstructor = clazz.getConstructor(ResultSet.class, Connection.class);

                if (resultSetConstructor != null) {
                    return resultSetConstructor.newInstance(source, connection);
                }
            } catch (Exception ignored) {}
            return (D) clazz.newInstance().parse(source, connection);
        }
    }

    @Override
    public Model parse(ResultSet response) throws RuntimeException {
        throw new RuntimeException("Operation is not supported while class is generic");
    }
}
