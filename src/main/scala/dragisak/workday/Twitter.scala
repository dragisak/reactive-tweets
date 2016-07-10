package dragisak.workday

import java.time.ZonedDateTime

case class Bearer(
  token_type: String,
  access_token: String
)

case class User(
  name: String,
  screen_name: String
)

case class Tweet(
  created_at: String,
  text: String,
  user: User
)

case class TwitterSearchResult(
  statuses: List[Tweet]
)


