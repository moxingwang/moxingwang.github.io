# Controller
```
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.moxingwang.demo.dto.Order;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
public class TestController {
    @GetMapping("/date")
    public Order localDate(
            @RequestParam(value = "date")
            @DateTimeFormat(pattern = "yyyy-MM-dd")
                    LocalDate localDate) {
        Order order = new Order();
        order.setLocalDate(localDate);
        return order;
    }

    @GetMapping("/datetime")
    public Order localDate(
            @RequestParam(value = "date")
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                    LocalDateTime dateTime) {
        Order order = new Order();
        order.setLocalDateTime(dateTime);
        return order;
    }

}
```

# dto
```
public class Order implements Serializable {
    private LocalDateTime localDateTime = LocalDateTime.now();
    private LocalDate localDate = LocalDate.now();


    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    public LocalDate getLocalDate() {
        return localDate;
    }

    public void setLocalDate(LocalDate localDate) {
        this.localDate = localDate;
    }
}

```

# test
* http://localhost:8080/date?date=2019-01-17
```aidl
{"localDateTime":"2019-01-17 19:47:51","localDate":"2019-01-17"}
```
* http://localhost:8080/datetime?date=2019-01-17%2018:31:41
```aidl
{"localDateTime":"2019-01-17 18:31:41","localDate":"2019-01-17"}
```