# healthchecker

One of our hobby project suffer from network connectivity problems, so we put a healthcheck service in all components. This service returns with a JSON in this format:

    {  
        "server":"https://node-aruba-1.balloonninja.net/HealthCheck",  
        "status":true,  
        "resources":{  
          "logstatus":true,  
          "mysql":true,  
          "redis":true  
        }
    }

I've created this mini health-checker application for fun, but we could use Nagios, Icinga or other monitoring tool instead of this one.
