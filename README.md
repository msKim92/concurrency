## STACK
Spring boot, JPA, MySQL, Redis (redisson)

#### 로직 내용  
100개 남은 재고에서 물건을 동시에 삭제했을때

#### synchronized로 해결
-> @transactional 사용시, 스레드마다 프록시가 생성되므로 감소후에 서로 충돌이 일어나면서 작동이 중지가 됨.
-> 또한 분산환경에서 서버가 2대이상이면 적용이안됨, `자바의 synchronized는 하나의 프로세스에서만 적용`되기 때문  
인스턴스단위로 thread-safe 이 보장이 되고, 여러서버가 된다면 여러개의 인스턴스가 있는것과 동일한 상황이기 때문이다.  
   
      
## Mysql

#### 비관락 
`충돌이 빈번하게 일어난다면 낙관락보다 더 효율이 좋을 수 있다`. 락을 통해 업데이트를 하기 때문에 데이터의 정합성을 어느정도 보장해준다.  
단점:  
-> 경쟁이 적다면 성능이 느려질수있다.  
-> timeout을 구현하기 어렵다.  

#### 낙관락    
별도의 락을 잡지않기떄문에 `성능상의 장점`이 있다.  
단점:   
-> 실패했을때 다시 시도할 수있는 방법을 구현해야한다.    

#### 네임드락  
-> 이름을 가진 메타데이터락이다. 비관락과는 다르게 별도의 공간에 락을 거는 작업이다. 멀티 DB로 구축 시 비관락으로 안되는 것을 해결할 수 있다.    

```java
   @Query(value = "select get_lock(:key, 3000)", nativeQuery = true)
    void getLock(String key);

    @Query(value = "select release_lock(:key)", nativeQuery = true)
    void releaseLock(String key);
```

단점:
트랜잭션이 종료될때 자동종료가 안되므로 별도의 종료요청이나 시간을 걸어서 락을 풀어야한다. getlock명령어  
여러 개의 락을 하나의 이름으로 묶어서 관리하기 때문에, 락의 순서를 잘못 지정하거나 동시에 여러 개의 락을 요청하는 상황에서 데드락이 발생할 가능성이 높다.  
   
   
## Redis  

#### Lettuce (재시도가 필요하지않은 경우)    
setnx 명렬어를 활용하여 분산락 구현  
spin lock방식 이기 때문에 동시에 많은 스레드가 lock 획득 대기 상태라면 redis에 부하가 갈 수 있다. -> 네임드락과 유사하다  
구현이 간단하다.  
spring data redis를 이용하면 lettuce가 기본방식이기떄문에 별도의 라이브러리를 사용하지않아도 된다.  

단점:
redis에 부하를 줄수있다.



#### Redisson (재시도가 필요한경우)  
pub-sub방식  
상대적으로 Lettuce에 비해 redis 부하를 줄여준다.  
락 획득 재시도를 기본으로 제공함.  
lock을 라이브러리 차원에서 제공해주기에 사용법을 알아야함.  


단점: 
별도의 라이브러리와 구현이 복잡하다.
