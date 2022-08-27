package de.bentzin.tools.register;


import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class Registerator<T> implements Iterable {

    private String name = this.getClass().getName() + hashCode();
    private List<T> index = new ArrayList<>();

    public Registerator() {
    }

    public Registerator(String name) {
        this.name = name;
    }

    public Registerator(List<T> startList) {
        index = startList;
    }

    public String getName() {
        return name;
    }

    public List<T> getIndex() {
        return index;
    }

    public void setIndex(List<T> index) {
        this.index = index;
    }

    /**
     * Register an unregistered Entry right here
     *
     * @param object extends T
     * @return object extends T
     * @throws DuplicateEntryException
     */
    public T register(T object) throws DuplicateEntryException {
        if (!index.contains(object)) {
            getIndex().add(object);
        } else
            throw new DuplicateEntryException(object, this);

        return object;
    }

    /**
     * This will allow you to unregister stuff!
     *
     * @param object
     * @return object
     * @throws NoSuchEntryException
     */
    public T unregister(T object) throws NoSuchEntryException {
        if (index.contains(object)) {
            getIndex().remove(object);
        } else
            throw new NoSuchEntryException(object, this);

        return object;
    }

    public boolean isRegistered(T object) {
        return index.contains(object);
    }


    @Override
    public String toString() {
        return "de.bentzin.tools.register.Registerator{" +
                ", index=" + index +
                '}';
    }

    public void clear() {
        getIndex().clear();
    }

    /**
     * Returns an iterator over elements of type {@code T}.
     *
     * @return an Iterator.
     */
    @NotNull
    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {


            @Override
            public boolean hasNext() {
                return getIndex().iterator().hasNext();
            }

            @Override
            public T next() {
                return getIndex().iterator().next();
            }
        };
    }

    public static class DuplicateEntryException extends Exception {

        String msg = "";

        public DuplicateEntryException(Object entry, Registerator registerator) {
            setMsg("de.bentzin.tools.register.Registerator \"" + registerator.getName() + "\" already contains the entry \"" + entry + "\"");
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        @Override
        public String getMessage() {
            return msg;
        }
    }

    public static class NoSuchEntryException extends Exception {

        String msg = "";

        public NoSuchEntryException(Object entry, Registerator registerator) {
            setMsg("de.bentzin.tools.register.Registerator \"" + registerator.getName() + "\" does not contain the entry \"" + entry + "\"");
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        @Override
        public String getMessage() {
            return msg;
        }
    }

    private class RegisteratorIterator<T> implements Iterator<T> {

        /**
         * Returns {@code true} if the iteration has more elements.
         * (In other words, returns {@code true} if {@link #next} would
         * return an element rather than throwing an exception.)
         *
         * @return {@code true} if the iteration has more elements
         */
        @Override
        public boolean hasNext() {
            return getIndex().iterator().hasNext();
        }

        /**
         * Returns the next element in the iteration.
         *
         * @return the next element in the iteration
         * @throws NoSuchElementException if the iteration has no more elements
         */
        @Override
        public T next() throws NoSuchElementException {
            // return getIndex().iterator().next();
            return null;
        }
    }
}
