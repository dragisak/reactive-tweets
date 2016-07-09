package dragisak.workday

case class GitHubProject (
  name: String,
  full_name: String,
  url: String
)

case class GitHubResponse(
  items: List[GitHubProject]
)
