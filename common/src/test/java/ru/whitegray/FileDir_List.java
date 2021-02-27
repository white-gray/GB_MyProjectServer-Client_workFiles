package ru.whitegray;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class FileDir_ListTest {
    private static FileDir_List fileDir_List;
    @BeforeAll
    public static void init() {
        fileDir_List = new FileDir_List();
    }

    @Test
    public void testShortPath() {
        Assertions.assertEquals("Клиент_2/weq", fileDir_List.shortPath("./qweqw/qwrf/qsdf/df/Клиент_2/weq"));
    }


    @Test
    public void testShortPath2() {
        Assertions.assertEquals("Клиент_55/eq", fileDir_List.shortPath("./qweqw/qwrf/qsdf/df/Клиент_2/weq"));
    }
}
