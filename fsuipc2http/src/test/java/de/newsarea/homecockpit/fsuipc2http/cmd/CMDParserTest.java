package de.newsarea.homecockpit.fsuipc2http.cmd;

import org.apache.commons.cli.ParseException;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertEquals;

public class CMDParserTest {

    @Test
    public void shouldReturnDefaults() throws Exception {
        CMDOptions options = new CMDParser(8080, 8081).parse(new String[] {});
        // then
        assertEquals(8080, options.getHttpPort());
        assertEquals(8081, options.getSocketPort());
    }

    @Test
    public void shouldReturnHTTPPortByShortOption() throws Exception {
        CMDOptions options = new CMDParser(8080, 8081).parse(new String[] { "-p", "1000"});
        assertEquals(1000, options.getHttpPort());
        assertEquals(8081, options.getSocketPort());
    }

    @Test
    public void shouldReturnHTTPPortByLongOption() throws Exception {
        CMDOptions options = new CMDParser(8080, 8081).parse(new String[] { "--http-port=1000"});
        assertEquals(1000, options.getHttpPort());
        assertEquals(8081, options.getSocketPort());
    }

    @Test
    public void shouldReturnSocketByShortOption() throws Exception {
        CMDOptions options = new CMDParser(8080, 8081).parse(new String[] { "-s", "1000"});
        assertEquals(1000, options.getSocketPort());
        assertEquals(8080, options.getHttpPort());
    }

    @Test
    public void shouldReturnSocketByLongOption() throws Exception {
        CMDOptions options = new CMDParser(8080, 8081).parse(new String[] { "--socket-port=1000"});
        assertEquals(1000, options.getSocketPort());
        assertEquals(8080, options.getHttpPort());
    }

    @Test(expectedExceptions = ParseException.class)
    public void shouldThrowParseException() throws Exception {
        new CMDParser(8080, 8081).parse(new String[] { "--invalid"});
    }

}
