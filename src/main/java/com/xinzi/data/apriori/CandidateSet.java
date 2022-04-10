package com.xinzi.data.apriori;

import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 *
 * @Description:
 * @Auther: xinzi
 * @Date: 2022/03/11/17:06
 */
public class CandidateSet {

    public Set<String> data;


    public CandidateSet(String key) {
        this.data = new HashSet<>();
        data.add(key);
    }

    public CandidateSet(CandidateSet candidateSet) {
        this.data = new HashSet<>();
        data.addAll(candidateSet.data);
    }


    public int size() {
        return data.size();
    }

    public void add(String key) {
        data.add(key);
    }

    public void remove(String key) {
        data.remove(key);
    }

    @Override
    public int hashCode() {
        int result = 0;
        for (String item : data) {
            result = result * 17 + item.hashCode();
        }
        return result;
    }

    @Override
    public String toString() {
        return data.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof CandidateSet)) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        CandidateSet candidateSet = (CandidateSet) obj;
        Set<String> data = candidateSet.data;
        if (this.data.size() != data.size()) {
            return false;
        }
        return this.data.containsAll(data) && data.containsAll(this.data);
    }

}
