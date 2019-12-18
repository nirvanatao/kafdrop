package kafdrop.config;

import lombok.Data;
import org.apache.kafka.clients.CommonClientConfigs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Properties;


@Component
@Data
public final class KafkaConfiguration {
  private static final Logger LOG = LoggerFactory.getLogger(KafkaConfiguration.class);

  private String brokerList;

  public void applyCommon(Properties props) {
    brokerList = System.getenv("KAFKA_BROKER_LIST");
    props.setProperty(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, brokerList);

    String val = System.getenv("SECURE_KAFKA");
    if (!Boolean.parseBoolean(val)) {
      return;
    }

    String saslPassword = System.getenv("KAFKA_CLIENT_PASSWORD");
    String saslUser = System.getenv("KAFKA_CLIENT_USER");
    String keyStorePath = System.getenv("KAFKA_CLIENT_KEYSTORE_PATH");
    String trustStorePath = System.getenv("KAFKA_CLIENT_TRUSTSTORE_PATH");
    String keyStorePassword = System.getenv("KAFKA_CLIENT_KEYSTORE_PWD");
    String trustStorePassword = System.getenv("KAFKA_CLIENT_TRUSTSTORE_PWD");

    props.setProperty("security.protocol", "SASL_SSL");

    props.setProperty("sasl.jaas.config", String.format("org.apache.kafka.common.security.scram.ScramLoginModule required username='%s' password='%s';", saslUser, saslPassword));
    props.setProperty("sasl.mechanism", "PLAIN");
    props.setProperty("sasl.kerberos.service.name", "kafka");

    props.setProperty("ssl.protocol", "TLSv1.2");
    props.setProperty("ssl.enabled.protocols", "TLSv1.2");

    props.setProperty("ssl.keystore.location", keyStorePath);
    props.setProperty("ssl.keystore.password", keyStorePassword);
    props.setProperty("ssl.keystore.type", "PKCS12");

    props.setProperty("ssl.truststore.location", trustStorePath);
    props.setProperty("ssl.truststore.password", trustStorePassword);
    props.setProperty("ssl.truststore.type", "PKCS12");
  }
}