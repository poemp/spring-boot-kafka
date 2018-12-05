package org.poem.dao;

import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

/**
 * @author poem
 */
@Repository
public class TopicDao {

    public List<String> top() {
        return Arrays.asList("TOP", "TEST", "LOGGER","DaiDai");
    }
}
