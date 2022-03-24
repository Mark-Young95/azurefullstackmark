package com.revature.AzureFullStackMark.beans.repos;

import com.revature.AzureFullStackMark.beans.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends JpaRepository<User, Integer> {
}
