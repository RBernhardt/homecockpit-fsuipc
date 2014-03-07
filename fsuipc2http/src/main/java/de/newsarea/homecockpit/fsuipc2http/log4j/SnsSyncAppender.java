package de.newsarea.homecockpit.fsuipc2http.log4j;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sns.AmazonSNSAsync;
import com.amazonaws.services.sns.AmazonSNSAsyncClient;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.LoggingEvent;

import java.util.Arrays;

public class SnsSyncAppender extends AppenderSkeleton {

    private AmazonSNSAsync sns;

    private String topicARN;
    private String accessKey;
    private String secretKey;

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public void setTopicARN(String topicARN) {
        this.topicARN = topicARN;
    }

    @Override
    protected void append(LoggingEvent event) {
        if(this.sns == null) {
            this.sns = new AmazonSNSAsyncClient(new BasicAWSCredentials(accessKey, secretKey));
        }
        //
        String logMessage;
        if (layout != null) {
            logMessage = layout.format(event);
        } else {
            logMessage = event.getRenderedMessage();
        }
        if (logMessage.getBytes().length > 64 * 1024) {
            // SNS has a 64K limit on each published message.
            logMessage = new String(Arrays.copyOf(logMessage.getBytes(), 64 * 1024));
        }

        try {
            String subject = event.getLoggerName() + " log: " + event.getLevel().toString();
            String body = logMessage;
            sns.publish(topicARN, body ,subject);
        } catch (AmazonClientException ase) {
            LogLog.error("Could not log to SNS", ase);
        }
    }

    public void close() {
        sns.shutdown();
        ((AmazonSNSAsyncClient) sns).getExecutorService().shutdownNow();
    }

    public boolean requiresLayout() {
        return false;
    }

}
