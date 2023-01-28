package com.ReRollBag.domain.entity;

import com.sun.istack.NotNull;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Bags {

    @Id
    @Column
    private String bagsId;

    @NotNull
    @Column
    private boolean isRented;

    @Column
    private LocalDateTime whenIsRented;

    @ManyToOne(fetch = FetchType.EAGER)
    private Users rentingUsers;

}
