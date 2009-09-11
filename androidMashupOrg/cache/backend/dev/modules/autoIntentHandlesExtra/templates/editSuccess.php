<?php use_helper('Object', 'Validation', 'ObjectAdmin', 'I18N', 'Date') ?>

<?php use_stylesheet('/sf/sf_admin/css/main') ?>

<div id="sf_admin_container">

<h1><?php echo __('edit IntentHandlesExtra', 
array()) ?></h1>

<div id="sf_admin_header">
<?php include_partial('IntentHandlesExtra/edit_header', array('intent_handles_extra' => $intent_handles_extra)) ?>
</div>

<div id="sf_admin_content">
<?php include_partial('IntentHandlesExtra/edit_messages', array('intent_handles_extra' => $intent_handles_extra, 'labels' => $labels)) ?>
<?php include_partial('IntentHandlesExtra/edit_form', array('intent_handles_extra' => $intent_handles_extra, 'labels' => $labels)) ?>
</div>

<div id="sf_admin_footer">
<?php include_partial('IntentHandlesExtra/edit_footer', array('intent_handles_extra' => $intent_handles_extra)) ?>
</div>

</div>
