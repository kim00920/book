package book.Book.user.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RatingResponse {
    private int ratingGroup;
    private int userCount;


}