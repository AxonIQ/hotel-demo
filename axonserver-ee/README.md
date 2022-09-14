## Running Axon Server EE in Docker with docker-compose

When running Axon Server in Docker under a non-root user, we need to be careful about ownership of files. On Linux hosts (including WSL) you'll need to check that the directories are publicly writable, which may be difficult if they are on a Windows drive.

### Required files

The example in this directory expects the following files to exist:

* A (valid) license file at "`../../axoniq.license`", and
* A copy of the Axon Server CLI at "`../../axonserver-cli.jar`". (for creating the admin user)

The Docker image used, "`axoniq/axonserver-enterprise:latest-dev`", is pulled from Docker Hub.

### Preparing the secrets

The deployment in this example uses secrets for the Axon Server properties file, the license, and the system token. The `create-secrets.sh` script will generate three UUID values for use as system token, internal token, and password for the "`admin`" user, and fill the internal token into the correct proprty in the properties file:

```sh
$ ./create-secrets.sh
Checking tokens
- Generating system-token
- Generating internal-token
- Generating admin password
Generating properties file
$
```

If any of the files already exists, it will be left unchanged.

If you prefer an insecure setup, you can either change the `axoniq.axonserver.accesscontrol.enabled` property in the `axonserver.properties.tmpl` file to `false` before you run the script, or put a simple password in `admin.password`.

### Start the cluster

To start the cluster, run the "`docker-compose up`" command and wait until you see it initialize the contexts. If you use "`docker-compose up -d`" instead, you will not see the logging everything will be started in the background so you can run new commands. Note that, as all three nodes are started at the same time, you may see errors and warnings about being unable to contact certain nodes or not having a leader in "`_admin`" or "`default`". These situations are temporary and startup should (eventually) progress towards a consistent state.

### Creating an Admin user

This example starts with access control enabled, so you will need a user account with the "`ADMIN@_admin`" role to be able to proceed with registering applucations, creatin Replication Groups and COntexts, etc. The `create-admin.sh` script can do this for you, connecting to the first node using the system token from "`axonserver.token`" and the password in "`admin.password`".

### Cleaning up

Run the "`cleanup.sh`" script to throw away all locally created files. Not this will not actually delete the Docker volumes, so they won't need to be recreated when you next run Axon Server.