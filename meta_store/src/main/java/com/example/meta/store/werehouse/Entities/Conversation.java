package com.example.meta.store.werehouse.Entities;

import java.io.Serializable;
import java.util.Set;

import com.example.meta.store.Base.Entity.BaseEntity;
import com.example.meta.store.Base.Security.Entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="conversation")
public class Conversation extends BaseEntity<Long> implements Serializable {

    private static final long serialVersionUID = 12345678112L;
    
    @ManyToOne
    private User user1;
    
    @ManyToOne
    private User user2;
    
    @JsonIgnore
    @OneToMany
    private Set<Message> message;
}
