package org.interlis2.validator.refmapping;

import java.io.File;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

public class RefMappingTest {
    private static final String TEST_IN="test/data/RefMapping/";
    @Test
    public void simple() throws Exception {
        RefMapping mapping=new RefMapping();
        mapping.addFile(new File(TEST_IN,"Simple.xtf"));
        String ret[]=mapping.getRefData("1",new String[]{"ModelA.TopicA"});
        Assert.assertEquals(3,ret.length);
        Assert.assertTrue(Arrays.binarySearch(ret,"ilidata:1")>=0);
        Assert.assertTrue(Arrays.binarySearch(ret,"ilidata:2")>=0);
        Assert.assertTrue(Arrays.binarySearch(ret,"ilidata:3")>=0);
    }
    @Test
    public void genericTopic() throws Exception {
        RefMapping mapping=new RefMapping();
        mapping.addFile(new File(TEST_IN,"Simple.xtf"));
        String ret[]=mapping.getRefData("2",new String[]{"ModelA.TopicB"});
        Assert.assertEquals(1,ret.length);
        Assert.assertTrue(Arrays.binarySearch(ret,"ilidata:20")>=0);
    }
    @Test
    public void genericScope() throws Exception {
        RefMapping mapping=new RefMapping();
        mapping.addFile(new File(TEST_IN,"Simple.xtf"));
        String ret[]=mapping.getRefData("3",new String[]{"ModelA.TopicC"});
        Assert.assertEquals(1,ret.length);
        Assert.assertTrue(Arrays.binarySearch(ret,"ilidata:30")>=0);
    }
    @Test
    public void multiTopic() throws Exception {
        RefMapping mapping=new RefMapping();
        mapping.addFile(new File(TEST_IN,"Simple.xtf"));
        String ret[]=mapping.getRefData("3",new String[]{"ModelA.TopicB","ModelA.TopicC"});
        Assert.assertEquals(2,ret.length);
        Assert.assertTrue(Arrays.binarySearch(ret,"ilidata:20")>=0);
        Assert.assertTrue(Arrays.binarySearch(ret,"ilidata:30")>=0);
    }
}
