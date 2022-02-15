package com.jasonzyt.mirai.githubbot

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

open class User {
    var id: Long = 0
    var node_id: String = ""
    var login: String = ""
    var type: String = ""
    var site_admin: Boolean = false
    // Member
    var permissions: Permissions? = null
    // Full information
    var name: String? = null
    var company: String? = null
    var blog: String? = null
    var location: String? = null
    var email: String? = null
    var hireable: String? = null
    var bio: String? = null
    var twitter_username: String? = null
    var public_repos: Int = 0
    var public_gists: Int = 0
    var followers: Int = 0
    var following: Int = 0
    var created_at: String? = null
    var updated_at: String? = null
    //var private_gists: Int = 0
    //var total_private_repos: Int = 0
    //var owned_private_repos: Int = 0
    //var disk_usage: Int = 0
    //var collaborators: Int = 0
    //var two_factor_authentication: Boolean = false
    //var plan: Plan? = null

    var url: String = ""
    var html_url: String = ""
    var avatar_url: String = ""
}

class Team : User()
class Sender : User()

class Organization {
    var id: Long = 0
    var node_id: String = ""
    var login: String = ""
    var description: String? = null

    var url: String = ""
    var avatar_url: String = ""
}

open class Committer {
    var name: String? = null
    var email: String? = null
    var username: String? = null
}

class Pusher : Committer()

class Repository {
    var id: Long = 0
    var node_id: String = ""
    var name: String = ""
    var full_name: String = ""
    var private: Boolean = false
    var owner: User? = null
    var url: String = ""
    var description: String? = null
    var fork: Boolean = false
    var created_at: String = ""
    var updated_at: String? = null
    var pushed_at: String? = null
    var git_url: String = ""
    var ssh_url: String = ""
    var clone_url: String = ""
    var svn_url: String = ""
    var homepage: String? = null
    var size: Int = 0
    var stargazers_count: Int = 0
    var watchers_count: Int = 0
    var subscribers_count: Int = 0
    var network_count: Int = 0
    var forks_count: Int = 0
    var open_issues_count: Int = 0
    var language: String? = null
    var has_issues: Boolean = false
    var has_projects: Boolean = false
    var has_downloads: Boolean = false
    var has_wiki: Boolean = false
    var has_pages: Boolean = false
    var mirror_url: String? = null
    var archived: Boolean = false
    var disabled: Boolean = false
    var license: License? = null
    var allow_forking: Boolean = false
    var is_template: Boolean = false
    var topics: List<String>? = null
    var default_branch: String = ""
    var temp_clone_token: String? = null
}

class Commit {
    class Tree {
        var url: String = ""
        var sha: String = ""
    }
    class Verification {
        var verified: Boolean = false
        var reason: String? = null
        var signature: String? = null
        var payload: String? = null
    }
    var id: String = ""
    var tree_id: String = ""
    var distinct: Boolean = false
    var message: String? = null
    var timestamp: String = ""
    var author: Committer? = null
    var committer: Committer? = null
    var added: List<String>? = null
    var removed: List<String>? = null
    var modified: List<String>? = null
    var comment_count: Int = 0
    var tree: Tree? = null
    var verification: Verification? = null

    var url: String = ""
}

class Issue {
    var id: Long = 0
    var node_id: String = ""
    var number: Int = 0
    var title: String = ""
    var state: String? = null
    var locked: Boolean? = null
    var user: User? = null
    var labels: List<Label>? = null
    var assignees: List<User>? = null
    var milestone: Milestone? = null
    var comments: Int = 0
    var created_at: String = ""
    var updated_at: String? = ""
    var closed_at: String? = null
    var author_association: String? = null
    var body: String? = null
    var active_lock_reason: String? = null
    var closed_by: User? = null
    var reactions: Reactions? = null
    // todo: PR&Project support
    var timeline_url: String? = null
    var parformed_via_github_app: Boolean? = null
}

class PullRequest {
    var id: Long = 0
    var node_id: String = ""
    var url: String = ""
    var number: Int = 0
    var state: String = ""
    var locked: Boolean = false
    var title: String = ""
    var user: User? = null
    var body: String? = null
    var created_at: String = ""
    var updated_at: String? = ""
    var closed_at: String? = null
    var merged_at: String? = null
    var merge_commit_sha: String? = null
    var assignee: User? = null
    var assignees: List<User>? = null
    var requested_reviewers: List<User>? = null
    var requested_teams: List<Team>? = null
    var labels: List<Label>? = null
    var milestone: Milestone? = null
    var draft: Boolean = false
    var head: PullRequestHead? = null
    var base: PullRequestBase? = null
    //var _links: Links? = null
    var author_association: String? = null
    var auto_merge: Boolean? = null
    var active_lock_reason: String? = null
    var merged: Boolean = false
    var mergeable: Boolean? = null
    var rebaseable: Boolean? = null
    var mergeable_state: String? = null
    var merged_by: User? = null
    var comments: Int = 0
    var review_comments: Int = 0
    var maintainer_can_modify: Boolean = false
    var commits: Int = 0
    var additions: Int = 0
    var deletions: Int = 0
    var changed_files: Int = 0
}

class PullRequestHead {
    var label: String = ""
    var ref: String = ""
    var sha: String = ""
    var user: User? = null
    var repo: Repository? = null
}

class PullRequestBase {
    var label: String = ""
    var ref: String = ""
    var sha: String = ""
    var user: User? = null
    var repo: Repository? = null
}

class Discussion {
    var id: Long = 0
    var node_id: String = ""
    var url: String = ""
    var number: Int = 0
    var category: Category? = null
    var body: String? = null
    var title: String? = null
    var user: User? = null
    var state: String? = null
    var locked: Boolean? = null
    var comments: Int = 0
    var created_at: String = ""
    var updated_at: String? = ""
    var reactions: Reactions? = null
    var author_association: String? = null
    var active_lock_reason: String? = null
    var answer_html_url: String? = null
    var answer_chosen_at: String? = null
    var answer_chosen_by: User? = null
}

class Category {
    var id: Long = 0
    var node_id: String = ""
    var name: String = ""
    var emoji: String = ""
    var slug: String = ""
    var description: String? = null
    var created_at: String = ""
    var updated_at: String? = null
    var is_answerable: Boolean = false
}

class Comment {
    var id: Long = 0
    var node_id: String = ""
    var url: String = ""
    var body: String = ""
    var user: User? = null
    var created_at: String = ""
    var updated_at: String? = ""
    var author_association: String? = null
    var reactions: Reactions? = null
    //var pull_request_review_id: Long? = null
    //var diff_hunk: String? = null
    var path: String? = null
    var position: Int? = null
    //var original_position: Int? = null
    var commit_id: String? = null
    //var original_commit_id: String? = null
    //var in_reply_to_id: Long? = null
    //var pull_request_url: String? = null
    var line: Int? = null
    var parent_id: Long? = null
    var child_comment_count: Int? = null
    var performed_via_github_app: Boolean? = null
}

class Label {
    var id: Long = 0
    var node_id: String = ""
    var url: String = ""
    var name: String = ""
    var color: String = ""
    var description: String = ""
    var default: Boolean = false
}

class Milestone {
    var id: Long = 0
    var node_id: String = ""
    var url: String = ""
    var labels_url: String = ""
    var number: Int = 0
    var state: String = ""
    var title: String = ""
    var description: String = ""
    var creator: User? = null
    var open_issues: Int = 0
    var closed_issues: Int = 0
    var created_at: String = ""
    var updated_at: String? = null
    var closed_at: String? = null
    var due_on: String? = null
}

class Reactions {
    var url: String = ""
    var total_count: Int = 0
    @SerializedName("+1")
    var plus: Int = 0
    @SerializedName("-1")
    var minus: Int = 0
    var laugh: Int = 0
    var hooray: Int = 0
    var confused: Int = 0
    var heart: Int = 0
    var rocket: Int = 0
    var eyes: Int = 0
}

class Release {
    class Asset {
        var id: Long = 0
        var node_id: String = ""
        var name: String = ""
        var label: String? = null
        var uploader: User? = null
        var content_type: String? = null
        var state: String = ""
        var size: Int = 0
        var download_count: Int = 0
        var created_at: String = ""
        var updated_at: String? = null
        var browser_download_url: String = ""

        var url: String = ""
    }
    var id: Long = 0
    var node_id: String = ""
    var tag_name: String = ""
    var target_commitish: String = ""
    var name: String? = null
    var body: String? = null
    var draft: Boolean = false
    var prerelease: Boolean = false
    var created_at: String = ""
    var published_at: String? = null
    var author: User? = null
    var assets: List<Asset>? = null

    var url: String = ""
    var html_url: String = ""
    var assets_url: String = ""
    var upload_url: String = ""
    var tarball_url: String = ""
    var zipball_url: String = ""
}

class License {
    var key: String = ""
    var name: String = ""
    var spdx_id: String = ""
    var url: String? = null
    var node_id: String = ""
}

class Page {
    var page_name: String = ""
    var title: String = ""
    var summary: String? = null
    var action: String = ""
    var sha: String? = null
    var html_url: String? = null
}

class Changes {
    class From {
        var from: String = ""
    }

    var title: From? = null
    var body: From? = null
    var name: From? = null
    var color: From? = null
    var description: From? = null
    var due_on: From? = null
}

class Permissions {
    var admin: Boolean = false
    var maintain: Boolean = false
    var push: Boolean = false
    var pull: Boolean = false
    var triage: Boolean = false
}

class Rule {
    var id: Long = 0
    var repository_id: Long = 0
    var name: String = ""
    var created_at: String = ""
    var updated_at: String? = null
    // todo: Rule support
    var repository: Repository? = null
}


enum class EventType {
    None,
    BranchProtectionRule,
    CheckRun,
    CheckSuite,
    CodeScanningAlert,
    CommitComment,             // Done
    Create,                    // Done
    Delete,                    // Done
    DeployKey,
    Deployment,
    DeploymentStatus,
    Discussion,                // Done
    DiscussionComment,         // Done
    Fork,                      // Done
    GithubAppAuthorization,
    Gollum,                    // Done
    Installation,
    InstallationRepositories,
    IssueComment,              // Done
    Issues,                    // Done
    Label,                     // Done
    MarketplacePurchase,
    Member,                    // Done
    Membership,
    Meta,
    Milestone,                 // Done
    Organization,
    OrgBlock,
    Package,
    PageBuild,
    Ping,
    Project,
    ProjectCard,
    ProjectColumn,
    Public,
    PullRequest,               // Done
    PullRequestReview,
    PullRequestReviewComment,
    Push,                      // Done
    Release,                   // Done
    RepositoryDispatch,
    Repository,                // Done
    RepositoryImport,
    RepositoryVulnerabilityAlert,
    SecretScanningAlert,
    SecurityAdvisory,
    Sponsorship,
    Star,                      // Done
    Status,
    Team,
    TeamAdd,
    Watch,                     // Done
    WorkflowDispatch,
    WorkflowJob,
    WorkflowRun;

    companion object {
        fun value(name: String): EventType {
            val realName = name.dropWhile { it == '_' }
            for (eventType in values()) {
                if (eventType.name.toLowerCase() == name) {
                    return eventType
                }
            }
            return EventType.None
        }
    }
}

enum class AuthorAssociation {
    None,
    Owner,
    Collaborator,
    Contributor,
    Maintainer,
    Author,
    Committer,
    Member;

    companion object {
        fun value(name: String): AuthorAssociation {
            val realName = name.toLowerCase().dropWhile { it == '_' }
            for (v in AuthorAssociation.values()) {
                if (v.name.toLowerCase() == name) {
                    return v
                }
            }
            return AuthorAssociation.None
        }
    }
}

enum class DiscussionAction {
    None,
    Created,
    Edited,
    Deleted,
    Pinned,
    Unpinned,
    Locked,
    Unlocked,
    Transferred,
    CategoryChanged,
    Answered,
    Unanswered,
    Labeled,
    Unlabeled;

    companion object {
        fun value(name: String): DiscussionAction {
            val realName = name.toLowerCase().dropWhile { it == '_' }
            for (v in DiscussionAction.values()) {
                if (v.name.toLowerCase() == name) {
                    return v
                }
            }
            return DiscussionAction.None
        }
    }
}

enum class PageAction {
    None,
    Created,
    Edited;

    companion object {
        fun value(name: String): PageAction {
            val realName = name.toLowerCase().dropWhile { it == '_' }
            for (v in PageAction.values()) {
                if (v.name.toLowerCase() == name) {
                    return v
                }
            }
            return PageAction.None
        }
    }
}

enum class IssueAction {
    None,
    Opened,
    Edited,
    Deleted,
    Pinned,
    Unpinned,
    Closed,
    Reopened,
    Assigned,
    Unassigned,
    Labeled,
    Unlabeled,
    Locked,
    Unlocked,
    Transferred,
    Milestoned,
    Demilestoned;

    companion object {
        fun value(name: String): IssueAction {
            val realName = name.toLowerCase().dropWhile { it == '_' }
            for (v in IssueAction.values()) {
                if (v.name.toLowerCase() == name) {
                    return v
                }
            }
            return IssueAction.None
        }
    }
}

enum class CommentAction {
    None,
    Created,
    Edited,
    Deleted;

    companion object {
        fun value(name: String): CommentAction {
            val realName = name.toLowerCase().dropWhile { it == '_' }
            for (v in CommentAction.values()) {
                if (v.name.toLowerCase() == name) {
                    return v
                }
            }
            return CommentAction.None
        }
    }
}

enum class LabelAction {
    None,
    Created,
    Edited,
    Deleted;

    companion object {
        fun value(name: String): LabelAction {
            val realName = name.toLowerCase().dropWhile { it == '_' }
            for (v in LabelAction.values()) {
                if (v.name.toLowerCase() == name) {
                    return v
                }
            }
            return LabelAction.None
        }
    }
}

enum class MemberAction {
    None,
    Added,
    Removed,
    Edited;

    companion object {
        fun value(name: String): MemberAction {
            val realName = name.toLowerCase().dropWhile { it == '_' }
            for (v in MemberAction.values()) {
                if (v.name.toLowerCase() == name) {
                    return v
                }
            }
            return MemberAction.None
        }
    }
}

enum class MilestoneAction {
    None,
    Created,
    Closed,
    Opened,
    Edited,
    Deleted;

    companion object {
        fun value(name: String): MilestoneAction {
            val realName = name.toLowerCase().dropWhile { it == '_' }
            for (v in MilestoneAction.values()) {
                if (v.name.toLowerCase() == name) {
                    return v
                }
            }
            return MilestoneAction.None
        }
    }
}

enum class PullRequestAction {
    None,
    Opened,
    Edited,
    Closed,
    Reopened,
    Synchronize,
    Assigned,
    Unassigned,
    Labeled,
    Unlabeled,
    ReviewRequested,
    ReviewRequestRemoved,
    ReadyForReview,
    Locked,
    Unlocked,
    Transferred,
    Demilestoned,
    Milestoned,
    AutoMergeDisabled,
    AutoMergeEnabled,
    ConvertedToDraft;

    companion object {
        fun value(name: String): PullRequestAction {
            val realName = name.toLowerCase().dropWhile { it == '_' }
            for (v in PullRequestAction.values()) {
                if (v.name.toLowerCase() == name) {
                    return v
                }
            }
            return PullRequestAction.None
        }
    }
}

enum class PullRequestReviewAction {
    Submitted,
    Edited,
    Dismissed;

    companion object {
        fun value(name: String): PullRequestReviewAction {
            val realName = name.toLowerCase().dropWhile { it == '_' }
            for (v in PullRequestReviewAction.values()) {
                if (v.name.toLowerCase() == name) {
                    return v
                }
            }
            return PullRequestReviewAction.Submitted
        }
    }
}

enum class ReleaseAction {
    None,
    Published,
    Unpublished,
    Edited,
    Deleted,
    Prereleased,
    Released;

    companion object {
        fun value(name: String): ReleaseAction {
            val realName = name.toLowerCase().dropWhile { it == '_' }
            for (v in ReleaseAction.values()) {
                if (v.name.toLowerCase() == name) {
                    return v
                }
            }
            return ReleaseAction.None
        }
    }
}

enum class RepositoryAction {
    None,
    Created,
    Deleted,
    Archived,
    Unarchived,
    Edited,
    Renamed,
    Transferred,
    Publicized,
    Privatized;

    companion object {
        fun value(name: String): RepositoryAction {
            val realName = name.toLowerCase().dropWhile { it == '_' }
            for (v in RepositoryAction.values()) {
                if (v.name.toLowerCase() == name) {
                    return v
                }
            }
            return RepositoryAction.None
        }
    }
}

enum class StarAction {
    None,
    Created,
    Deleted;

    companion object {
        fun value(name: String): StarAction {
            val realName = name.toLowerCase().dropWhile { it == '_' }
            for (v in StarAction.values()) {
                if (v.name.toLowerCase() == name) {
                    return v
                }
            }
            return StarAction.None
        }
    }
}

open class Event (
    val type: EventType = EventType.None,
    open val guid: String
) {
    var action: String? = null
    var sender: Sender? = null

    var ref: String? = null
    var ref_type: String? = null
    var master_branch: String? = null
    var description: String? = null
    var pusher_type: String? = null
    //var number: Int? = null
    var before: String? = null
    var after: String? = null
    var created: Boolean? = null
    var deleted: Boolean? = null
    var forced: Boolean? = null
    var base_ref: String? = null
    var compare: String? = null
    var starred_at: String? = null

    var organization: Organization? = null
    var repository: Repository? = null
    var comment: Comment? = null
    var forkee: Repository? = null
    var issue: Issue? = null
    var discussion: Discussion? = null
    var pages: List<Page>? = null // Gollum
    var changes: Changes? = null
    var assignee: User? = null
    var label: Label? = null
    var member: User? = null
    var milestone: Milestone? = null
    var pull_request: PullRequest? = null
    var requested_reviewer: User? = null
    var head_commit: Commit? = null
    var commits: List<Commit>? = null
    var pusher: Pusher? = null
    var release: Release? = null
    var commit: Commit? = null

    companion object {
        fun fromJson(str: String): Event? {
            return try {
                Gson().fromJson(str, Event::class.java)
            } catch (e: Exception) {
                PluginMain.logger.error("Failed to parse event JSON: $str")
                PluginMain.logger.error(e)
                null
            }
        }
    }
}
