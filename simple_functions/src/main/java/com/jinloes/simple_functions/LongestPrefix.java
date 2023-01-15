package com.jinloes.simple_functions;

import java.util.List;
import java.util.Map;

/**
 * hostnames:
 * host-a:
 * ips: [10.1.1.1/16, 10.1.1.2/24, 10.1.1.3/28]
 * configure: yes
 * interface: eth1
 * host-b:
 * ips: [10.20.1.1/16, 10.20.1.2/28, 10.1.1.3/28]
 * configure: yes
 * <p>
 * <p>
 * {
 * "hostnames": {
 * "host-a": {
 * "ips": [
 * "10.1.1.1/16",
 * "10.1.1.2/24",
 * "10.1.1.3/28"
 * ],
 * "url": "host-a.prisma.com",
 * "configure": "yes",
 * "interface": "eth1"
 * },
 * "host-b": {
 * "ips": [
 * "10.20.1.1/16",
 * "10.20.1.2/28",
 * "10.1.1.3/28"
 * ],
 * "configure": "yes"
 * }
 * }
 * }
 * <p>
 * <p>
 * <p>
 * # Goal is to configure an ip on the specified interface for each host in the yaml with the ip that has the longest prefix.
 * # 1. check if "configure" flag is set to true, if yes then check if config interface is given, if yes then configure that interface with the ip that has the longest prefix
 * # 2. if "configure" is true but no interface is provided throw appropriate errors.
 * # Assume: you have access to a class login which can be used to send the config command [ifconfig <interface name> ip]
 * <p>
 * <p>
 * # what are the unit tests you will write for this? no need to code
 */
public class LongestPrefix {
    private class Host {
        List<String> ips;
        String configure;
        String interfaceName;

        public Host(List<String> ips, String configure, String interfaceName) {
            this.ips = ips;
            this.configure = configure;
            this.interfaceName = interfaceName;
        }
    }

    private class Hostname {
        Map<String, Host> hosts;

        public Hostname(Map<String, Host> hosts) {
            this.hosts = hosts;
        }
    }

    public void longestPrefix(Hostname hostname) {
        if(hostname == null) {
            return;
        }

        for (Host host : hostname.hosts.values()) {
            String longestPrefixIp = findLongestPrefixIp(host.ips);
            if ("yes".equals(host.configure)
                    && host.interfaceName != null
                    && !host.interfaceName.isEmpty()
                    && longestPrefixIp != null) {
                System.out.println(String.format("ifconfig %s %s", host.interfaceName, longestPrefixIp));
            }
        }
    }

    private String findLongestPrefixIp(List<String> ips) {
        int maxPrefixSize = 0;
        String maxPrefixIp = null;

        for (String ipStr : ips) {
            String[] parts = ipStr.split("//");
            String ip = parts[0];
            int prefixSize = Integer.parseInt(parts[1]);
            if (maxPrefixSize < prefixSize) {
                maxPrefixIp = ip;
                maxPrefixSize = prefixSize;
            }
        }
        return maxPrefixIp;
    }
}
