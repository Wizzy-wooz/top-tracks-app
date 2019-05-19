# Top 10 tracks from LastFM
## Setup 
1. Check application.conf file and adjust it according to your needs.
1. Run following commands: 
 
 ## 1. Clean project

    sbt clean
    
 ## 2. Compile project
 
    sbt compile
       
## Running TopTracksApp class via sbt

    sbt topTracksApp

## Running using docker

    sbt docker
    docker images 
    docker run --rm -it <DOCKER_IMAGE_ID>

## Running test via sbt

    sbt test

## Running via IDE (etc IntellejIdea): 


    just run TopTracksApp 
    
## Expected result
After running top-tracks-app on the dataset which contains songs played 
by Last.fm's users: http://mtg.upf.edu/static/datasets/last.fm/lastfm-dataset-1K.tar.gz
one should get the following result of a list of top 10 songs played in the top 50 longest 
user sessions by tracks count. Each user session may be comprised of one or more songs 
played by that user, where each song is started within 20 minutes of the previous song's 
start time:

1. Jolene                               
1. Heartbeats                           
1. How Long Will It Take                
1. Anthems For A Seventeen Year Old Girl
1. St. Ides Heaven                      
1. Bonus Track                          
1. Starin' Through My Rear View         
1. Beast Of Burden                      
1. The Swing                            
1. When You Were Young       

## Expected time of execution approximately 5 minutes on machine:
Hardware Overview:
  * Model Name:	MacBook Pro
  * Processor Name:	Intel Core i7
  * Processor Speed:	2.2 GHz
  * Number of Processors:	1
  * Total Number of Cores:	4
  * L2 Cache (per Core):	256 KB
  * L3 Cache:	6 MB
  * Memory:	16 GB

## Improvements required:
  1. Provide docker compose to dockerized this App in cluster
  1. Optimize dataframes' sql queries.
