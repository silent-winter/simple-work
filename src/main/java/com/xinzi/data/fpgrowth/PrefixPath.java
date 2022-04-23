package com.xinzi.data.fpgrowth;

import org.apache.commons.lang3.StringUtils;

import java.util.LinkedList;

/**
 * Created with IntelliJ IDEA.
 *
 * @Description:
 * @Auther: xinzi
 * @Date: 2022/03/11/12:47
 */
public class PrefixPath implements Comparable<PrefixPath> {

    private final LinkedList<String> path;


    public PrefixPath() {
        this.path = new LinkedList<>();
    }

    public PrefixPath(String key) {
        this.path = new LinkedList<>();
        path.addFirst(key);
    }

    public void add(String key) {
        path.addFirst(key);
    }

    public LinkedList<String> getPath() {
        return path;
    }

    public int size() {
        return path.size();
    }

    public boolean containsAll(PrefixPath path) {
        return this.path.containsAll(path.path);
    }

    @Override
    public int hashCode() {
        int result = 0;
        for (String key : path) {
            result = result * 17 + key.hashCode();
        }
        return result;
    }

    @Override
    public String toString() {
        return path.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof PrefixPath)) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        PrefixPath prefixPath = (PrefixPath) obj;
        LinkedList<String> nPath = prefixPath.getPath();
        if (path.size() != nPath.size()) {
            return false;
        }
        for (int i = 0; i < path.size(); i++) {
            if (!StringUtils.equals(path.get(i), nPath.get(i))) {
                return false;
            }
        }
        return true;
    }


    @Override
    public int compareTo(PrefixPath o) {
        LinkedList<String> path = o.getPath();
        if (path.containsAll(this.path) && this.path.containsAll(path)) {
            return 0;
        }
        if (path.size() != this.path.size()) {
            return Integer.compare(path.size(), this.path.size());
        }
        return 1;
    }
}
