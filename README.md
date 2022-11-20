# webflux_redis

app gets data from http://localhost:7634/aircraft(you need to run planefinder app)
and save it in its db

it uses lomback lib\
you can use for data class this annotations
@Data - annotation equivalent methods equals(), hashcode(), toString(), getters and setters\
@NoArgsConstructor, @AllArgsConstructor, equivalent constructor without argument and constructor with all arguments

@RequiredArgsConstructor - you can use this annotation for inject data from data class to other classes
@NonNull