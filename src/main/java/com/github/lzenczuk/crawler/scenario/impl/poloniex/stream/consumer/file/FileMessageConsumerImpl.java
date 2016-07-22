package com.github.lzenczuk.crawler.scenario.impl.poloniex.stream.consumer.file;

import com.github.lzenczuk.crawler.scenario.impl.poloniex.stream.Message;
import com.github.lzenczuk.crawler.scenario.impl.poloniex.stream.consumer.MessageConsumer;
import com.github.lzenczuk.crawler.scenario.impl.poloniex.stream.consumer.MessageConsumerException;
import com.github.lzenczuk.crawler.scenario.impl.poloniex.stream.json.JsonMessageMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.zip.GZIPOutputStream;

/**
 * Created by dev on 20/07/16.
 */
public class FileMessageConsumerImpl implements MessageConsumer{

    private static final Logger logger = LogManager.getLogger(FileMessageConsumerImpl.class);

    private final String dataFileName;
    private final JsonMessageMapper jsonMessageMapper;
    private OutputStream fileOutputStream;

    private final AtomicBoolean closed = new AtomicBoolean(true);

    public FileMessageConsumerImpl(String dataFileName) throws MessageConsumerException {
        this(dataFileName, new JsonMessageMapper());
    }

    public FileMessageConsumerImpl(String dataFileName, JsonMessageMapper jsonMessageMapper) throws MessageConsumerException {
        this.dataFileName = dataFileName;
        this.jsonMessageMapper = jsonMessageMapper;

        try {
            fileOutputStream = new GZIPOutputStream(new FileOutputStream(dataFileName));
            closed.set(false);

        } catch (FileNotFoundException e) {
            throw new MessageConsumerException(e.getMessage());
        } catch (IOException e) {
            throw new MessageConsumerException(e.getMessage());
        }
    }

    @Override
    public void consume(List<Message> messages) {

        messages.forEach(message -> {
            try {
                if(!closed.get()) {
                    fileOutputStream.write((jsonMessageMapper.formatMessage(message)+"\n").getBytes());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        try {
            fileOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void close() throws IOException {
        closed.set(true);
        logger.info("GZ file closed");
        fileOutputStream.close();
    }
}
