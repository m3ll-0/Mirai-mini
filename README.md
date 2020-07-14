# Mirai-mini
Mirai botnet rewritten in Java. This project covers the bot-segment of Mirai and doesn't (yet) support the CNC-segment.

### Installation and setup

1. Download the project and open it in IntelliJ or download the binary in the list below. 
2. Create the app.config file to supply as the mandatory config parameter (-c / --config)
3. Adjust the settings, the only settings that need immediate change are the database parameter. As of right now Mirai-mini only supports MariaDB. 
4. Profit.

app.config example:

```sh
# The number IP's generated per loop
# 100 = low, 250 = medium, 500 = high, 1000 >=very high
app.GENERATE_IP_PER_LOOP=400

# The number of IP scanner threads in the thread pool.
app.IPSCANNER_THREADPOOL_MAX_THREADS=1000

# The maximum number of concurrent SSH threads
app.MAX_SSH_THREADS=3

# The maximum number of concurrent TELNET threads
app.MAX_TELNET_THREADS=1

# Latency between SSH threads
app.THREAD_SSH_LATENCY=5000

# SO Timeout in ms latency of telnet thread
app.THREAD_TELNET_SO_TIMEOUT=7000

# Delay in ms of SSH DB reporter thread
app.DB_THREAD_DELAY=20000

# Supress output (makes program faster)
app.SUPPRESS_OUTPUT=false

# Database configuration variables
app.MARIADB_SERVER=127.0.0.1
app.MARIADB_DATABASE=XXX
app.MARIADB_USER=XXX
app.MARIADB_PASS=XXX
```

### Binaries

This section will soon be updated.
