# Git Branch Naming and Commit Guidelines for Spring Boot Project

## 1. Git Branch Naming and Commit Messages

- Branches for features should follow the format `feature/<keyword>`, for example `feature/login-endpoint`. The part after `feature/` should be a set of keywords describing the feature.
- Branches for bugs should follow the format `bugfix/<description>`, for example `bugfix/missing-avatar-image`. The part after `bugfix/` should describe the issue briefly.
- Commit messages for a Spring Boot project should follow the format:  
  `<component>/<file>: <action> <description>`  
  Example commit messages include:  
  - `user/authentication: add login endpoint`
  - `user/controller: fix user profile update`
  - `notification/email: add email verification method`
  - `notification/message: improve notification format`
  - `user/repository: add findByUserId method`
  - `user/security: update password encryption`
  - `notification/notification: remove deprecated method`

- Commits should be atomic, meaning each commit should focus on one task or change.

## 2. Pull Request Description

In the description of a Pull Request (PR), it is necessary to add the keyword `Closes:` followed by the link to the GitHub issue being closed by the PR.  
Example:  
`Closes: #27`  

**PR Title**: [Add a brief description of the changes made in the PR, following the format: "Implement <feature> or Fix <issue>"]
