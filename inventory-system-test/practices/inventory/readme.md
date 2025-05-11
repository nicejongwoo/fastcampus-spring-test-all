## setting docker mysql inventory db

> https://github.com/nicejongwoo/dev-env/tree/main/mysql

```
docker exec -it local-mysql8 mysql -uroot -p
password: root

create database inventory;

use inventory;

show tables;

select * from inventory;
```