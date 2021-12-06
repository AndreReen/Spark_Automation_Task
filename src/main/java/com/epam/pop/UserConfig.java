package com.epam.pop;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

@Component
public class UserConfig implements Serializable {

    public List<String> garbage;
    public List<String> test;

    @Value("${garbage}")
    private void setGarbage(String[] garbage) {
        this.garbage = Arrays.asList(garbage);
    }
    @Value("${test}")
    private void setTest(String[] test) {
        this.test = Arrays.asList(test);
    }
}