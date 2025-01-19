package com.shahkaar.cloud_functions.data;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class Employee {
    private String id;
    private String fName;
    private String lName;
    private String role;
}
