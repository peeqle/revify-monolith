package com.revify.monolith.user.models.user.additional.rating;


import com.revify.monolith.user.models.user.additional.Reaction;
import io.vavr.Tuple2;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Data
public class UserRating implements Serializable {
    @Serial
    private static final long serialVersionUID = 8286482L;

    private Double rating;

    private Long ratingCount;

    private List<Tuple2<Reaction, Long>> reactions = new LinkedList<>();

    public void calculateRating(Double rating) {
        this.rating = ((this.rating * ratingCount) + rating) / ++this.ratingCount;
    }

    public void addReactions(List<Reaction> reactions) {
        reactions.forEach(this::addReaction);
    }

    public void addReaction(Reaction reaction) {
        Optional<Tuple2<Reaction, Long>> existingReaction = reactions.stream().filter(el -> el._1().equals(reaction)).findFirst();

        if (this.reactions.isEmpty() || existingReaction.isEmpty()) {
            this.reactions.add(new Tuple2<>(reaction, 1L));
        } else {
            Tuple2<Reaction, Long> reactionLongTuple2 = existingReaction.get();
            reactions.removeIf(el -> el._1().equals(reaction));
            reactions.add(reactionLongTuple2.update2(reactionLongTuple2._2() + 1L));
        }
    }
}
