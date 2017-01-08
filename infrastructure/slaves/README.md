This describes how to setup a generic slave. 
--------------------------------------------

It is simple to create a new slave. Create a Ubuntu 14.04 new machine and run the `setup.sh`.

# AWS
This is the process of creating a slave on AWS. There is no need for ever SSH:ing to the machine or anything, only UI on web.
1. Access the AWS Console
2. Go to EC2 
3. Go to correct region (Ireland)
4. Click `Launch instance`
5. Select `Ubuntu Server 14.04 LTS (HVM)`, click `Next`.
6. Select an instance type equal to or greater than `small`, click `Next`.
7. Under Advanced details, add the contents from `setup.sh` to the text area (keep `As text` checked)
Modify the content `MASTER_IP` to the private ip of the master instance. Click `Next`.
8. Add some storage. Note a machine can run multiple services which means multiple images. Pick something between 30 - 40gb. Click `Next`.
9. Add tags. Name should symbolise slave. Add an `Owner` tag and enter your name. Click `Next`.
10. Time for security groups. Select existing and pick `micro-workshop-users`. If you pick wrong, the slave won´t be able to communicate with other services. Click `Next`
11. On review page, review and click `Launch`.
12. Pick an existing key pair or create a new. Click `Launch`

The instance will now get created and connect as a slave to Rancher automatically. Wait for it to initialize (look at AWS Console). Once ready, you should be able to find the
machine in Rancher under `Infrastructure/Hosts`
