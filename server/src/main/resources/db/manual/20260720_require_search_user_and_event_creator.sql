-- Align PostgreSQL schema with the JPA model for required owner relationships.
-- Run this manually before starting the application with ddl-auto=validate.

-- Search history is only persisted for authenticated users. Orphan rows cannot
-- be shown in any user's history, so they can be removed before enforcing NOT NULL.
delete from search_entry
where user_id is null;

-- Events created/imported from FIOS must keep an audit user.
-- If production already has orphan events, assign them to a real admin/user first:
-- update musical_event
-- set created_by_user_id = <fallback_user_id>
-- where created_by_user_id is null;

alter table search_entry
  alter column user_id set not null;

alter table musical_event
  alter column created_by_user_id set not null;
