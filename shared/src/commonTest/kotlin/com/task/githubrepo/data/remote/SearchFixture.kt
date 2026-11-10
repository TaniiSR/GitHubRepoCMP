package com.task.githubrepo.data.remote

/** Trimmed real GitHub search response (3 items). */
const val SEARCH_JSON = """
{
  "total_count": 374012,
  "incomplete_results": false,
  "items": [
    {
      "id": 23096959,
      "name": "go",
      "full_name": "golang/go",
      "description": "The Go programming language",
      "language": "Go",
      "stargazers_count": 108415,
      "owner": {
        "login": "golang",
        "avatar_url": "https://avatars.githubusercontent.com/u/4314092?v=4"
      }
    },
    {
      "id": 34526884,
      "name": "ant-design",
      "full_name": "ant-design/ant-design",
      "description": "An enterprise-class UI design language and React UI library",
      "language": "TypeScript",
      "stargazers_count": 84307,
      "owner": {
        "login": "ant-design",
        "avatar_url": "https://avatars.githubusercontent.com/u/12101536?v=4"
      }
    },
    {
      "id": 44838949,
      "name": "swift",
      "full_name": "apple/swift",
      "description": "The Swift Programming Language",
      "language": "C++",
      "stargazers_count": 61787,
      "owner": {
        "login": "apple",
        "avatar_url": "https://avatars.githubusercontent.com/u/10639145?v=4"
      }
    }
  ]
}
"""

/** Same items plus unknown fields and invalid items (no full_name, no owner, null stars). */
const val SEARCH_JSON_WITH_INVALID_ITEMS = """
{
  "total_count": 374012,
  "incomplete_results": false,
  "items": [
    {
      "id": 23096959,
      "name": "go",
      "full_name": "golang/go",
      "description": "The Go programming language",
      "language": "Go",
      "stargazers_count": 108415,
      "owner": {
        "login": "golang",
        "avatar_url": "https://avatars.githubusercontent.com/u/4314092?v=4"
      },
      "node_id": "MDEwOlJlcG9zaXRvcnkyMzA5Njk1OQ==",
      "forks": 1
    },
    {
      "id": 34526884,
      "name": "ant-design",
      "full_name": "ant-design/ant-design",
      "description": "An enterprise-class UI design language and React UI library",
      "language": "TypeScript",
      "stargazers_count": 84307,
      "owner": {
        "login": "ant-design",
        "avatar_url": "https://avatars.githubusercontent.com/u/12101536?v=4"
      },
      "node_id": "MDEwOlJlcG9zaXRvcnkyMzA5Njk1OQ==",
      "forks": 1
    },
    {
      "id": 44838949,
      "name": "swift",
      "full_name": "apple/swift",
      "description": "The Swift Programming Language",
      "language": "C++",
      "stargazers_count": 61787,
      "owner": {
        "login": "apple",
        "avatar_url": "https://avatars.githubusercontent.com/u/10639145?v=4"
      },
      "node_id": "MDEwOlJlcG9zaXRvcnkyMzA5Njk1OQ==",
      "forks": 1
    },
    {
      "id": 1,
      "name": "x",
      "description": "no owner",
      "stargazers_count": 5
    },
    {
      "id": 2,
      "name": "y",
      "owner": {
        "login": "someone",
        "avatar_url": null
      },
      "stargazers_count": 3
    },
    {
      "id": 3,
      "name": "nostars",
      "full_name": "o/nostars",
      "description": null,
      "language": null,
      "node_id": "MDEw",
      "forks": 12,
      "owner": {
        "login": "o",
        "avatar_url": null,
        "type": "User"
      }
    }
  ]
}
"""
