package com.xinzi.data.fpgrowth;

import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 *
 * @Description:
 * @Auther: xinzi
 * @Date: 2022/03/11/12:47
 */
public class PrefixPath {

    private final Set<String> path;


    public PrefixPath() {
        this.path = new HashSet<>();
    }

    public PrefixPath(String key) {
        this.path = new HashSet<>();
        path.add(key);
    }

    public void add(String key) {
        path.add(key);
    }

    public Set<String> getPath() {
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
        Set<String> nPath = prefixPath.getPath();
        if (path.size() != nPath.size()) {
            return false;
        }
        return path.containsAll(nPath) && nPath.containsAll(path);
    }

}
