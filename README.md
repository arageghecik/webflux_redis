# webflux_redis

app gets data from http://localhost:7634/aircraft (you need to run planefinder app)
and save it in its db

if you use RedisTamplate, you need to to create some set and get methods for converting time data to string. but if you use repositories there isn,t necessary to create special get and set convertors for that fildes, convertation will be automatically

if you uses lomback lib\
you can use for data class this annotations\
@Data - annotation equivalent methods equals(), hashcode(), toString(), getters and setters\
@NoArgsConstructor, @AllArgsConstructor, equivalent constructor without argument and constructor with all arguments\

@RequiredArgsConstructor - you can use this annotation for inject data from data class to other classes\
@NonNull
