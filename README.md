# filesDump

Simple file storage service. 
Interaction with the server goes via rest api:

/rest/userFile/{userPassword}/{username}/{pathToFile}:

  get: download file
  
  post: upload
  
  delete: delete
  
  
/rest/userDirectory/{userPassword}/{username}/{pathToDirectory}:

  get: get list of files in directory in JSON.
    example: there are text file and directory in a directory named 'dir' - '1.txt' and 'dirName'.
    get request will return this:
      
      [
        {
          "filename":"1.txt",
          "createTime":<<TIME IN MILLISECONDS SCINCE 1.01.1971>>,
          "lastModified":<<TIME IN MILLISECONDS SCINCE 1.01.1971>>,
          "isDirectory":false,
          "fileSize":<<FILE SIZE IN BYTES>>,
          "fileType":"text/plain"
        },
        
        {
          "filename":"dirName",
          "createTime":<<TIME IN MILLISECONDS SCINCE 1.01.1971>>,
          "lastModified":<<TIME IN MILLISECONDS SCINCE 1.01.1971>>,
          "isDirectory":true,
          "fileSize":<<SIZE OF ALL FILES INSIDE IN BYTES>>,
          "fileType":"unknown"
        }           
     ]
     
  post: create new directory
  
  delete: delete
  
Properties

There are property files in WEB-INF/properties catalog:

  db.properties:
    contains information about how to connect to the database. 
      example:
      
        db.url=jdbc:mysql://localhost:3306/filesdump?useLegacyDatetimeCode=false&serverTimezone=UTC&characterEncoding=utf8
        db.driver=com.mysql.cj.jdbc.Driver

        db.login=root
        db.pass=root
        
  global.properties:
    contains information about where must be root directory in which all users' files are stored;
    also sets catalog in what temporary multipart files will be stored.
    example: 
    
      baseDirectoryPath=C:\\files\\
      tmpStorageForMultipartFiles=C:\\multipart
      
  test.properties:
    same as global.propperties, but is used when tests run.
    
    
Database

    Just 2 tables are required:
    
    
    create table `users`(
	    id int not null primary key,
      email varchar(150) not null,
      password varchar(150) not null
    );

    create table `authorities`(
      id int not null primary key,
        auth varchar(50) not null,
        user_id int
    );
    
